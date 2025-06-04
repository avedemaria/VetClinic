# Vet Clinic Android App

## Описание

Прототип Android-приложения для ветеринарной клиники. Приложение позволяет пользователю записываться на приём к врачу, управлять информацией о своих питомцах, а администратору — просматривать и управлять списком приёмов.

Приложение реализовано с соблюдением принципов архитектуры MVVM с разделением на слои `presentation`, `domain`, `data`. Используется современный стек Android-разработки с фокусом на модульность, читаемость и тестируемость кода.

---

## Функциональные возможности

### Общие:
- Аутентификация и восстановление пароля через **Supabase** (включая поддержку **deep links**)
- Разделение ролей: **администратор** и **пользователь**
- Уведомления через **WorkManager** (за час до приёма)
- Хранение пользовательских данных с помощью **DataStore**
- Частичное покрытие unit-тестами с использованием **JUnit** и **MockK**
- Ограничения доступа к данным реализованы через **RLS (Row-Level Security)** в Supabase

---

### Пользователь:
- **Главный экран** с BottomNavigationView:
    - Домашняя страница
    - Экран записи к врачу
    - Экран списка приёмов
- **Экран профиля:**
    - Изменение имени и номера телефона
    - Переключение на экран питомцев (ToggleButton)
- **Управление питомцами:**
    - Добавление
    - Удаление (свайп в RecyclerView)
- **Запись к врачу:**
    - Список врачей
    - Детальная информация об услугах врача
    - Экран выбора времени и питомца
    - Диалог подтверждения
    - Перенаправление на экран приёмов
- **Экран приёмов:**
    - Актуальные и архивные приёмы
    - Статусы приёмов

> *Временные слоты генерируются на клиенте (в демонстрационных целях). В реальных условиях рекомендуется генерировать их на серверной стороне.*

---

### Администратор:
- **Экран приёмов на текущий день**
- Возможность выбрать дату (через DatePickerDialog)
- Пагинация через **Paging 3** + **RemoteMediator**
- Отображение списка приёмов с использованием **RecyclerView**

---

## Технологии

### Архитектура и принципы:
- MVVM
- Clean Architecture (presentation/domain/data)
- Dependency Injection: Hilt

### UI и навигация:
- Fragment-based UI
- Navigation Component
- RecyclerView, GridLayoutManager
- BottomNavigationView

### Сетевой и локальный доступ:
- API: Retrofit
- Supabase (авторизация, хранение данных, RLS)
- Room (локальная БД)
- Paging 3 + RemoteMediator (для списка приёмов администратора)
- DataStore (для хранения настроек и пользовательских данных)

### Тестирование:
- Unit-тесты: JUnit
- Моки: MockK
- 
---

## TODO

Проект находится в активной разработке и рефакторинге. В планах:

- Улучшить генерацию временных слотов — перенести логику на сервер.
- Расширить функционал для администраторов (например, управление врачами и услугами).
- Расширить покрытие тестами.
- Внедрить UI-улучшения и анимации для лучшего UX.


## Установка и запуск

1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/your-username/vet-clinic-app.git
   ```
2. Создайте файл local.properties в корне проекта:

   ```bash
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=eyJhbGciOi... #твой ключ
   ```
3. Откройте проект в Android Studio.

4.Убедитесь, что установлены необходимые зависимости и SDK:

5.Запустите приложение на устройстве или эмуляторе.

---

## Supabase настройки (только для демонстрации)

Для демонстрационного запуска приложения вы можете использовать встроенные значения:

   ```bash
   SUPABASE_URL=https://shuxcjnbzcrpkrtszccr.supabase.co
   SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNodXhjam5iemNycGtydHN6Y2NyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk1NDU0MjgsImV4cCI6MjA1NTEyMTQyOH0.f0c1YOXkqUHm9IoEA7MqoQP3GzI3MZZGcdnTQob3Ju8
   ```
Внимание: Этот ключ предназначен только для демонстрационных целей. Все доступы ограничены RLS-политиками на стороне Supabase. В продакшене следует использовать серверные токены и защищённые механизмы.

--- 

## Скриншоты

<p align="center">
  <b>Главная страница</b><br>
  <img src="assets/screenshot_home_page.jpg" width="300" alt="Главная страница">
</p>

<p align="center">
  <b>Список врачей</b><br>
  <img src="assets/screenshot_doctors.jpg" width="300" alt="Список врачей">
</p>

<p align="center">
  <b>Информация о враче</b><br>
  <img src="assets/screenshot_doctor_detailed_info.jpg" width="300" alt="Детальная информация о враче">
</p>

<p align="center">
  <b>Формирование записи</b><br>
  <img src="assets/screenshot_book_appointment.jpg" width="300" alt="Формирование записи">
</p>

<p align="center">
  <b>Список приёмов</b><br>
  <img src="assets/screenshot_appointments.jpg" width="300" alt="Список приёмов">
</p>

<p align="center">
  <b>Профиль пользователя </b><br>
  <img src="assets/screenshot_profile.jpg" width="300" alt="Профиль пользователя>
</p>

<p align="center">
  <b>Питомцы пользователя</b><br>
  <img src="assets/screenshot_pets.jpg" width="300" alt="Питомцы">
</p>

---

## Лицензия

Этот проект распространяется под лицензией MIT.