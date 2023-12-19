
# Custom Vanilla Alerts

This is a mod that can modify vanilla alerts such as when a player joins, leaves or dies.


## Screenshots
![Mod Screenshot](https://cdn.modrinth.com/data/if37LIFA/images/cd843c6e0aeecccdeb1765eb73b1a751393f7b5e.png)

![Mod Screenshot2](https://cdn.modrinth.com/data/if37LIFA/images/986c3ca0b0e9382602fbb4ecfed89dd54bd53c55.png)


## Usage
**General Command**:

/cva: Base command that groups all the mod's functions.
Setting:
Reload Settings:

/cva config reload: Reloads the mod configuration from the configuration file.
Restore Settings to Default Values:

/cva config restore: Restores the mod configuration to default values.
Change Message Visibility:

**Example of Use**:
/cva changeVisibility <visibility> <MessageType>

Change Visibility of Message Types:

/cva changeVisibility <true/false> DeathMessages: Enables or disables death messages.

/cva changeVisibility <true/false> DisconnectMessages: Enables or disables disconnect messages.

/cva changeVisibility <true/false> JoinMessages: Enables or disables join messages.

**Keywords in Messages:**

Add Words: /cva words addWords MessageType MessageId Content Color Style: Adds a keyword to the specified message.

Delete Words: /cva words removeWords MessageType MessageId ContentWord: Removes a keyword from the specified message.

**Custom Messages:**

Create Messages:
/cva message createMessage <MessageType> <MessageId>: Creates a new message of the specified type with the given identifier.

Delete messages:
/cva message deleteMessage <MessageType> <MessageId>: Deletes the message of the specified type with the given identifier.

**Additional notes**:

visibility: Boolean value (true/false) indicating whether visibility is enabled or disabled.

MessageType: Message type (death, disconnect, join).

MessageId: Unique identifier of the message.

Content: Content of the message.

Color: Color of the message.

Style: Style of the message.
