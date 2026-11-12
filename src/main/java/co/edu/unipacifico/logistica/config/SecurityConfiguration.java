package co.edu.unipacifico.logistica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Permisos por rol. Las reglas se evalúan en orden: la primera que coincide decide.
 *
 * Administrador          todo, incluida la gestión de usuarios.
 * Coordinador logístico  catálogos, entradas, salidas, transferencias, despachos, inventario y reportes.
 * Auxiliar de bodega     consulta catálogos; registra entradas, salidas y transferencias;
 *                        despacha (pone en ruta) y consulta inventario y reportes.
 * Transportador          solo sus despachos: los ve y registra la entrega o la devolución.
 */
@Configuration
public class SecurityConfiguration {

    private static final String ADMIN = "ADMIN";
    private static final String COORDINADOR = "COORDINADOR";
    private static final String AUXILIAR = "AUXILIAR_BODEGA";
    private static final String TRANSPORTADOR = "TRANSPORTADOR";

    private static final String[] CATALOGOS = {"/proveedores", "/clientes", "/categorias", "/bodegas", "/productos"};

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/registro", "/login", "/acceso-denegado",
                                 "/css/**", "/js/**", "/images/**").permitAll()

                .requestMatchers("/usuarios/**").hasRole(ADMIN)

                // Catálogos: crear, editar y eliminar solo administrador y coordinador; consultar también el auxiliar.
                .requestMatchers(rutas(CATALOGOS, "/crear", "/editar/**", "/eliminar/**")).hasAnyRole(ADMIN, COORDINADOR)
                .requestMatchers(rutas(CATALOGOS, "/**")).hasAnyRole(ADMIN, COORDINADOR, AUXILIAR)

                // Movimientos de bodega
                .requestMatchers("/entradas/*/anular", "/salidas/*/anular").hasAnyRole(ADMIN, COORDINADOR)
                .requestMatchers("/entradas/**", "/salidas/**", "/transferencias/**",
                                 "/existencias/**", "/movimientos/**", "/reportes/**").hasAnyRole(ADMIN, COORDINADOR, AUXILIAR)

                // Despachos
                .requestMatchers("/despachos/crear", "/despachos/*/cancelar", "/despachos/*/asignar").hasAnyRole(ADMIN, COORDINADOR)
                .requestMatchers("/despachos/*/despachar").hasAnyRole(ADMIN, COORDINADOR, AUXILIAR)
                .requestMatchers("/despachos/*/entregar", "/despachos/*/devolver").hasAnyRole(ADMIN, COORDINADOR, TRANSPORTADOR)
                .requestMatchers("/despachos/**").hasAnyRole(ADMIN, COORDINADOR, AUXILIAR, TRANSPORTADOR)

                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/principal", true)
                .failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
            .exceptionHandling(e -> e.accessDeniedPage("/acceso-denegado"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Combina cada módulo con cada sufijo: {"/productos"} y {"/crear"} → "/productos/crear". */
    private static String[] rutas(String[] modulos, String... sufijos) {
        String[] resultado = new String[modulos.length * sufijos.length];
        int i = 0;
        for (String m : modulos) {
            for (String s : sufijos) {
                resultado[i++] = m + s;
            }
        }
        return resultado;
    }
}
