# Intern Bot Notifier
A Spring Boot application that monitors DOM changes on [CSE Internship](https://internship.cse.hcmut.edu.vn) and sends notifications via a Telegram bot. Built with Java, Spring Boot, Jsoup, and the Telegram Bot API.

## :rocket: Features

1. Periodically checks the webpage [CSE Internship](https://internship.cse.hcmut.edu.vn) for changes in its HTML content, meaning that a new job entries is on.
2. Sends notifications to a Telegram chat when changes are detected.
3. Configurable via a ```telegram_credentials.json``` file, which is ignored by Git for security.

## ⚙️ Tech Stack
- **Java 21**
- **Spring Boot 3.4.4**
- **JSoup 1.16.1**
- **Telegrambots Spring Boot 6.9.7.1**


## 🔧 Installation
### 1. Clone the repository
```
git clone https://github.com/lamcao1206/intern-bot-notifier.git
cd intern-bot-notifier
```
### 2. Set up your credentials
Create a file named ```telegram_credentials.json``` in the root directory of your project.
This file will store the Telegram bot token and chat ID.

Example of telegram_credentials.json:
```
  {
  "telegrambot": {
    "username": <your_chatbot_name>,
    "token": <your_token_chatbot>,
    "chat_id": <your_chat_id_box>
  }
}
```
Note: Ensure that this file is git-ignored to keep it secure. It is automatically added to .gitignore by default. the guideline for getting those information is in the below part.

### 3. Configure the application
Open src/main/resources/application.properties and configure the URL of the webpage to monitor and any other preferences (such as the monitoring interval).
Example:
```
target.url=https://cse.internship.hcmut
tracker.interval=60
```

### 4. Run the application
You can run the application using the following Maven command:
```
mvn spring-boot:run
```
This will start the application and begin monitoring the webpage for changes.

## 📲 How to Get Telegram Bot Credentials
To set up the Telegram bot and retrieve the required information, follow these steps:

### 1. Create a Telegram Bot
1. Open Telegram: Download and open the Telegram app if you haven’t already.
2. Search for @BotFather:
- In the search bar, type BotFather and select the official bot.
- Create a New Bot:
- Start a chat with BotFather.
- Type /newbot and follow the prompts.
- BotFather will ask you to give your bot a name (e.g., InternBot).
- After that, it will ask for the bot’s username (this must end with bot, e.g., intern_bot).
### 2. Save the Token:
- Once the bot is created, BotFather will give you a token to access the Telegram Bot API.
- Copy the token, as you will need it for your ```telegram_credentials.json```.
### 3. Get the Chat ID
- To send notifications to your Telegram chat, you need the chat ID where the bot will send messages.
#### 1. Start a chat with your bot:
- Search for your bot by its username (e.g., intern_bot).
- Start a conversation with it (send any message to your bot).
#### 2. Get your chat ID:
- Open your browser and visit the following URL, replacing your-token-here with the token you obtained from BotFather:
```https://api.telegram.org/bot<your-token-here>/getUpdates```
- Look for the chat ID:
After sending a message to your bot, the response will include a JSON object with information about the messages, including the chat ID.
For example, the response might look like this:
```
{
  "ok": true,
  "result": [
    {
      "update_id": 123456789,
      "message": {
        "message_id": 1,
        "from": {
          "id": 1234567890,
          "is_bot": false,
          "first_name": "Your Name",
          "username": "yourusername",
          "language_code": "en"
        },
        "chat": {
          "id": -1234567890123,
          "first_name": "Your Name",
          "last_name": "Last Name",
          "username": "yourusername",
          "type": "private"
        },
        "date": 1609459200,
        "text": "Hello"
      }
    }
  ]
}
```
The chat.id field (-1234567890123 in this example) is your chat ID.

## 🛠️ Usage

Once the application is running, it will:

- Check the specified webpage (e.g., cse.internship.hcmut) every 60 seconds for any changes in the DOM.
- If new job postings are detected, it sends an instant notification to the configured Telegram chat.

## 📝 License

This project is licensed under the MIT License.
