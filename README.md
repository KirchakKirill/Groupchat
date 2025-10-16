# Group Chat App

Учебный проект для группового общения с обменом текстовых  и медиа  сообщений в реальном времени.
https://github.com/KirchakKirill/group-chat-backend - backend часть (написан на фреймворке Ktor)

## 📱 Скриншоты приложения

### Экран аутентификации
<p align="center">
  <img width="200" alt="image" src="https://github.com/user-attachments/assets/4b55a3a6-35f8-4942-81c6-7b0290933106" />
  <img width="200" alt="image" src="https://github.com/user-attachments/assets/55ad68dc-755a-4c88-8262-7909dd453694" />
  <img width="200" alt="image" src="https://github.com/user-attachments/assets/01a82f4b-b277-483c-9e40-646d22c824db" />
</p>

### Основные экраны
<p align="center">
  <img width="200"  alt="image" src="https://github.com/user-attachments/assets/6a7fb30f-88b1-4ed6-88e4-f85b2729ea97" />
  <img width="200" alt="image" src="https://github.com/user-attachments/assets/a15dcce7-45a0-442b-a72a-f74a5a112a6a" />
  <img width="300"  alt="image" src="https://github.com/user-attachments/assets/fd11f8d5-ad40-4cd5-93f3-6634dd468219" />
</p>

## 🚀 Технологии и стек

### Основные технологии
- **Kotlin** - основной язык разработки
- **Jetpack Compose** 

### Архитектура и DI
- **Koin** - dependency injection
- **MVVM** - архитектурный паттерн
- **Android ViewModel** - управление UI состоянием

### Сетевое взаимодействие
- **Retrofit** - HTTP клиент
- **Gson** - конвертер для JSON
- **OkHttp3** - клиент с поддержкой интерцепторов
- **Logging Interceptor** - логирование сетевых запросов

### Асинхронность
- **Kotlin Coroutines** - асинхронное программирование
- **LiveData** - наблюдение за изменениями данных

### Локальная база данных
- **Room** - БД
- **KSP** - обработка аннотаций Room

### Навигация
- **Navigation Compose** - навигация между экранами

### Дополнительные библиотеки
- **Coil** - загрузка и кэширование изображений
- **ThreeTenABP** - работа с датой и временем
- **DataStore** - хранение настроек и данных
- **Kotlin Dotenv** - управление переменными окружения
- **Android Credentials** - управление учетными данными

## 🎯 Функциональность

### 🔐 Аутентификация
- Регистрация нового пользователя
- Вход в систему ( в том числе с помощью Google)
- Валидация входных данных
- Безопасное хранение учетных данных

### 💬 Управление чатами
- Просмотр списка доступных чатов
- Создание новых групповых чатов
- Участие в существующих чатах

### ✉️ Обмен сообщениями
- Отправка и получение сообщений в реальном времени
- Возможность отправлять изображения и видео
- Отображение истории сообщений
