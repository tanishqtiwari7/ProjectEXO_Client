# ProjectEXO - Encrypted Chat Application 🔒💬

A secure chat application with RSA encryption, built with Java and Swing for the GUI.

## Features ✨

- 🔐 End-to-end RSA encryption for secure communication
- 👥 General chat room for all users
- 💌 Private messaging between users
- 🤖 AI Chat bot with Gemma3 as default LLM using Ollama API
- 🎬 Custom video splash screen using JavaFX
- 🔔 Desktop notifications for new messages
- 👤 Online user status tracking
- 🌙 Dark mode UI with FlatLaf

## System Requirements 🖥️

- Java 21 or newer
- Maven 3.8+ (for building from source)
- JavaFX 23+ (included as Maven dependency)
- Windows, macOS, or Linux operating system

## Building from Source 🛠️

### Using Maven Directly

```bash
# Clone the repository
git clone https://github.com/UdayKhare09/ProjectEXO_Client.git
cd ProjectEXO_Client

# Build the project
mvn clean package
```

### Using Build Scripts

**Windows:**
```
build.cmd
```

**Linux/macOS:**
```
chmod +x build.sh
./build.sh
```

## Running the Application 🚀

### Using Run Scripts

**Windows:**
```
run.cmd
```

**Linux/macOS:**
```
chmod +x run.sh
./run.sh
```

### Manually

```bash
java -jar target/ProjectEXO_Client-1.0-SNAPSHOT.jar
```

## Usage Guide 📝

1. **Launch** - The application begins with a custom video splash screen
2. **Login** - Enter your username and password when prompted
3. **Navigate** - Use the feature panel to select features
4. **Chat** - Select "Chat" and choose "General" for group chat or a user for private messaging
5. **AI Assistant** - Select "AI Chat bot" to interact with the AI assistant that supports Markdown formatting
6. **Send Messages** - Type your message and press Enter or click Send

## How It Works 🧩

- The client connects to the server using sockets
- RSA key exchange establishes a secure connection
- Messages are encrypted before sending and decrypted upon receipt
- The user interface is built with Java Swing
- AI responses are rendered with Markdown support using Flexmark
- The splash screen uses JavaFX Media to display a video during startup

## Customization 🎨

### Splash Screen
Place your own `splash.mp4` video file in the `src/main/resources` directory to customize the splash screen.

## Companion Project 🤝

This Client works with the [ProjectEXO](https://github.com/UdayKhare09/ProjectEXO) Server.

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact Information 📞

- **Developer:** Uday Khare
- **Email:** udaykhare77@gmail.com
- **LinkedIn:** https://linkedin.com/in/uday-khare-a09208289
- **Portfolio:** https://portfolio.udaykhare.social
- **GitHub:** UdayKhare09

## License ⚖️

This project is licensed under the [GNU General Public License v3 (GPLv3)](LICENSE).