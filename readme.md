an application through which you can quickly download or send saves of your non-steam games from your home computer to the Steam Deck or vice versa.

in development...

<!--
@startuml

class Main{
+void main
}

class Secrets{
String login
String password

+void setLogin()
+void setPassword()
}

class App{
+void sendSavesToSteamDeck(String save_location, String save_location_on_steam_deck)
+void getSavesFromSteamDeck(String save_location, String save_location_on_steam_deck)
+void createShortcut()

+void closeApp()
}

class GameConfig{
int game_id
String game_name
String save_location
String save_location_on_steam_deck

+void editNameGame(String game_name)
+void editSaveLocation(String game_location)
+void editGameId(int game_id)
}

class Backups extends GameConfig{

+void createBackupFolder(String game_id, String game_name)
+void backupSave(String game_id, String game_name)
+void backup(String game_id, String game_name)
}

Main --> App: Execute
App --> Secrets
App --> GameConfig
App --> Backups


@enduml
-->
