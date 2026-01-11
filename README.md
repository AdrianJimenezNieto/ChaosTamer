# 🌪️ ChaosTamer

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**ChaosTamer** es una solución de gestión de tareas Kanban de nivel empresarial. Diseñada bajo principios de arquitectura limpia, ofrece una experiencia de usuario fluida con sincronización en tiempo real y persistencia robusta.

<video src="https://github.com/usuario/repo/raw/main/docs/demo.webm" 
  width="100%" 
  autoplay 
  loop 
  muted 
  playsinline>
</video>

---

## 🚀 Stack Tecnológico

### Backend (`chaostamer-api`)
Núcleo robusto diseñado para la escalabilidad y el desacoplamiento.
- **Framework**: Spring Boot 3.x
- **Arquitectura**: Hexagonal (Ports & Adapters)
- **Base de Datos**: PostgreSQL 15 (Dockerizada)
- **Seguridad**: Spring Security + JWT + Filtros personalizados
- **Tiempo Real**: WebSocket (STOMP protocol)
- **Testing**: JUnit 5 & Mockito

### Frontend (`chaostamer-client`)
SPA moderna optimizada para rendimiento y mantenibilidad.
- **Core**: React 19 + TypeScript + Vite
- **Estilos**: TailwindCSS 3.4
- **Drag & Drop**: `@dnd-kit/core` & `@dnd-kit/sortable`
- **Gestión de Estado**: Zustand (con persistencia local)
- **Comunicación**: Axios (REST) + `@stomp/stompjs` (WebSockets)

---

## 🏗️ Arquitectura y Decisiones Técnicas

Este proyecto no es solo un CRUD; es una demostración de patrones de diseño avanzados.

### Backend: Arquitectura Hexagonal
Hemos huido de la arquitectura tradicional de capas para implementar **Puertos y Adaptadores**.
* **Dominio Puro**: El núcleo (`domain/model`) no tiene dependencias de frameworks (ni siquiera Spring annotations), garantizando que la lógica de negocio sea agnóstica a la infraestructura.
* **Puertos (Ports)**: Interfaces que definen los casos de uso (`port.in`) y las necesidades de salida (`port.out`).
* **Adaptadores (Adapters)**: Implementaciones concretas.
    * *Input*: REST Controllers y WebSocket Controllers separados por paquetes.
    * *Output*: Persistencia JPA y Seguridad desacoplados del dominio.

### Frontend: Separation of Concerns (SoC)
El cliente sigue el patrón **Container/Presentational** potenciado por Custom Hooks.
* **Custom Hooks**: Toda la lógica de negocio, llamadas a API y gestión de WebSockets reside en hooks como `useBoardLogic` y `useBoardWebSocket`.
* **Componentes "Tontos" (Dumb Components)**: Componentes como `BoardHeader` o `CreateListForm` son puramente visuales y reciben datos/acciones vía props, facilitando su testeo y reutilización.
* **Tipado Centralizado**: Definiciones de TypeScript segregadas por dominio (`types/board.types.ts`, `types/api.types.ts`) para evitar acoplamiento.

---

## ✨ Características Clave

* ✅ **Gestión Kanban Completa**: Crear, editar, eliminar y reordenar tableros, listas y tarjetas.
* ✅ **Sincronización Real-Time**: Los movimientos de otros usuarios se reflejan instantáneamente en tu pantalla sin recargar (WebSockets).
* ✅ **Optimistic UI**: La interfaz se actualiza antes de recibir confirmación del servidor para una sensación de latencia cero.
* ✅ **Seguridad Robusta**: Autenticación Stateless con JWT y protección de endpoints.
* ✅ **Drag & Drop Accesible**: Implementación suave y accesible mediante `dnd-kit`.