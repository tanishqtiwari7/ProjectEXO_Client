# ProjectEXO - Encrypted Chat Application 🔒💬

A secure chat application with RSA encryption, built with Java and Swing for the GUI.

## Features ✨

- 🔐 End-to-end RSA encryption for secure communication
- 👥 General chat room for all users
- 💌 Private messaging between users
- 🔔 Desktop notifications for new messages
- 👤 Online user status tracking
- 🌙 Dark mode UI with FlatLaf

## System Requirements 🖥️

- Java 21 or newer
- Maven 3.8+ (for building from source)
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

1. **Login** - Enter your username and password when prompted
2. **Navigate** - Use the feature panel to select "Chat"
3. **Chat** - Select "General" for group chat or a specific user for private messaging
4. **Send Messages** - Type your message and press Enter or click Send

## How It Works 🧩

- The client connects to the server using sockets
- RSA key exchange establishes a secure connection
- Messages are encrypted before sending and decrypted upon receipt
- The user interface is built with Java Swing

## Companion Project 🤝

This server works with the [ProjectEXO](https://github.com/UdayKhare09/ProjectEXO) application.

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
