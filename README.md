# 🌌 ChaosTamer - Kanban Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=white)](https://reactjs.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)](#-arquitectura)

**ChaosTamer** es una plataforma robusta de gestión de proyectos inspirada en metodologías Agile. A diferencia de clones convencionales, este proyecto ha sido diseñado siguiendo principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y **Clean Code**, garantizando una separación total entre la lógica de negocio y la infraestructura.

> **Status:** El backend está completamente migrado a una estructura de dominio desacoplada, ofreciendo una API REST segura y escalable.

---

## 🏗️ Arquitectura

El núcleo de ChaosTamer reside en su organización. Se ha implementado **Arquitectura Hexagonal** para asegurar que el dominio sea independiente de frameworks y herramientas externas:



* **Domain:** Contiene los modelos de negocio (`Board`, `Card`, `TaskList`) y los puertos (interfaces) que definen las reglas de la aplicación.
* **Infrastructure:** Adaptadores de entrada (Web/REST Controllers) y salida (Persistencia con JPA/PostgreSQL).
* **Application:** Casos de uso específicos que orquestan el flujo de datos entre los puertos.

## 🛠️ Stack Tecnológico

### Backend (Java / Spring Boot)
* **Spring Security & JWT:** Autenticación stateless y protección de recursos por propietario.
* **Spring Data JPA:** Gestión de persistencia con **PostgreSQL**.
* **Lombok & MapStruct:** Reducción de código repetitivo y mapeo eficiente entre Entidades y DTOs.
* **Arquitectura Hexagonal:** Desacoplamiento total de la lógica de negocio.

### Frontend (React / TypeScript)
* **DND-kit:** Implementación de Drag & Drop complejo para reordenamiento de tarjetas y listas.
* **Zustand:** Gestión de estado global ligera y eficiente.
* **Tailwind CSS:** Diseño moderno, oscuro y responsivo.
* **Optimistic Updates:** Interfaz ultra-rápida que actualiza el estado local antes de confirmar con el servidor.

---

## 🔥 Funcionalidades Clave

* **Gestión de Tableros:** Creación, edición y eliminación de espacios de trabajo personalizados.
* **Sistema de Listas Dinámicas:** Organización de tareas mediante columnas con soporte para reordenamiento horizontal.
* **Tarjetas Interactivas:** Drag & Drop vertical y entre columnas con persistencia de orden en base de datos.
* **Seguridad por Dueño:** Un usuario solo puede ver, editar o eliminar los tableros de los que es propietario, validado a nivel de servicio.
* **Edición Inline:** Modificación rápida de títulos de tareas y tableros mediante clics directos.