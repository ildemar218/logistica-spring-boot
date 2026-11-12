/*
 * Filas dinámicas del detalle (órdenes de compra y despachos).
 * - Agregar / quitar filas y renumerar los nombres detalles[i].campo para que Spring los reciba.
 * - Al elegir un producto, propone el valor unitario (costo o precio de venta).
 * - Muestra un total estimado. El servidor siempre recalcula los importes al guardar.
 * - En despachos, muestra el stock disponible del producto en la bodega elegida.
 */
(function () {
  var tabla = document.getElementById('detalle');
  if (!tabla) { return; }

  var cuerpo = tabla.querySelector('tbody');
  var plantilla = document.getElementById('plantillaFila');
  var opciones = document.getElementById('opcProductos');
  var atributoPrecio = tabla.getAttribute('data-precio');      // "costo" o "venta"
  var selBodega = document.getElementById('bodegaId');
  var stock = window.STOCK || null;                           // solo en despachos
  var TASA_IVA = 0.19;
  var formato = new Intl.NumberFormat('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

  function llenarProductos(fila) {
    var sel = fila.querySelector('.producto');
    var elegido = sel.getAttribute('data-selected') || sel.value;
    sel.innerHTML = '';
    var vacia = document.createElement('option');
    vacia.value = '';
    vacia.textContent = 'Seleccione...';
    sel.appendChild(vacia);
    Array.prototype.forEach.call(opciones.options, function (o) {
      sel.appendChild(o.cloneNode(true));
    });
    sel.value = elegido || '';
    sel.removeAttribute('data-selected');
  }

  function disponible(productoId) {
    if (!stock || !selBodega || !selBodega.value || !productoId) { return null; }
    var porProducto = stock[selBodega.value] || {};
    return porProducto[productoId] || 0;
  }

  function actualizarFila(fila) {
    var sel = fila.querySelector('.producto');
    var celdaDisp = fila.querySelector('.col-disponible');
    if (celdaDisp) {
      var d = disponible(sel.value);
      celdaDisp.textContent = d === null ? '—' : d;
      var cantidad = parseInt(fila.querySelector('.cantidad').value, 10) || 0;
      celdaDisp.style.color = d !== null && cantidad > d ? '#f87171' : '';
    }
    var cant = parseFloat(fila.querySelector('.cantidad').value) || 0;
    var valor = parseFloat(fila.querySelector('.valor').value) || 0;
    fila.querySelector('.subtotal').textContent = formato.format(cant * valor);
  }

  function actualizarTotales() {
    var subtotal = 0;
    Array.prototype.forEach.call(cuerpo.querySelectorAll('tr.fila'), function (fila) {
      actualizarFila(fila);
      var cant = parseFloat(fila.querySelector('.cantidad').value) || 0;
      var valor = parseFloat(fila.querySelector('.valor').value) || 0;
      subtotal += cant * valor;
    });
    var iva = Math.round(subtotal * TASA_IVA * 100) / 100;
    document.getElementById('estSubtotal').textContent = formato.format(subtotal);
    document.getElementById('estIva').textContent = formato.format(iva);
    document.getElementById('estTotal').textContent = formato.format(subtotal + iva);
  }

  function renumerar() {
    Array.prototype.forEach.call(cuerpo.querySelectorAll('tr.fila'), function (fila, i) {
      Array.prototype.forEach.call(fila.querySelectorAll('[name]'), function (campo) {
        campo.name = campo.name.replace(/detalles\[\d+\]/, 'detalles[' + i + ']');
      });
    });
  }

  function enlazar(fila) {
    var sel = fila.querySelector('.producto');
    sel.addEventListener('change', function () {
      var o = sel.options[sel.selectedIndex];
      if (o && o.value) {
        fila.querySelector('.valor').value = o.getAttribute('data-' + atributoPrecio);
      }
      actualizarTotales();
    });
    fila.querySelector('.cantidad').addEventListener('input', actualizarTotales);
    fila.querySelector('.valor').addEventListener('input', actualizarTotales);
    fila.querySelector('.quitar').addEventListener('click', function () {
      if (cuerpo.querySelectorAll('tr.fila').length === 1) { return; }  // siempre queda una fila
      fila.remove();
      renumerar();
      actualizarTotales();
    });
  }

  Array.prototype.forEach.call(cuerpo.querySelectorAll('tr.fila'), function (fila) {
    llenarProductos(fila);
    enlazar(fila);
  });

  document.getElementById('agregarFila').addEventListener('click', function () {
    cuerpo.appendChild(document.importNode(plantilla.content, true));
    var fila = cuerpo.lastElementChild;
    renumerar();
    llenarProductos(fila);
    enlazar(fila);
    actualizarTotales();
  });

  if (selBodega) { selBodega.addEventListener('change', actualizarTotales); }

  // Despachos: al elegir el cliente, propone su dirección como dirección de entrega.
  var selCliente = document.getElementById('clienteId');
  var direccion = document.getElementById('direccionEntrega');
  if (selCliente && direccion) {
    selCliente.addEventListener('change', function () {
      var o = selCliente.options[selCliente.selectedIndex];
      if (o && o.value && !direccion.value) {
        direccion.value = o.getAttribute('data-direccion') || '';
      }
    });
  }

  actualizarTotales();
})();
