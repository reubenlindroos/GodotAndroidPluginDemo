General Notes:
-------------

1. Use Windows 10. Oculus doesnt seem to have much support for other OS's for its drivers.

2. If you have SteamVR and/or the oculus rift dashboard system isntalled, remove them if possible. They interfered with the dialogues cropping up on the headset and kept changing my headset to the "teathered" mode. These software packages are also absolutely awful and should probably be listed as malware by most systems if possible. 

2. Download godot (tested on Godot 3.4.4 but later versions are probably okay)

3. Follow instructions to export for android: https://docs.godotengine.org/en/stable/tutorials/export/exporting_for_android.html#doc-exporting-for-android
NOTES:
	(i)  make sure to install OpenJRE 11, not later versions
	(ii) install the dependencies required by godot then run the emulator to check the one-click deploy system works. If you can generate a 2D main.tscn on the emulator you are good to go. 

4.  Follow the instructions to deploy to the quest 2 https://docs.godotengine.org/en/stable/tutorials/vr/oculus_mobile/developing_for_oculus_quest.html
Ammendements:
	(i) When adjusting the export settings in godot, select "Regular" rather than "Oculus Mobile VR" under xr features. The Oculus Mobile VR API is deprecated and doesnt support remote debugging.
 	(ii) Setting up the quest for developement https://developer.oculus.com/documentation/native/android/mobile-device-setup/:
	      - You will need to make yourself the "owner" of the device (if you arent already). This means if there is already an account on the device you will either need to turn this account into a developper account or factory reset the device and go from there (i went for the second option). To factory reset the device hold in the power button + lower volume button with the device off. 
	      - Once you have a developper account (that also has admin rights) download the Oculus app on your phone. The headset will then link to it and you can enable developper mode from there.
 	      - Install ODH (oculus developper hub) instead of installing the oculus ADB driver. ODH seems like a very useful tool for measuring performance etc. It allows for casting, which means you can check the display without having ot take the headset on and off all the time (FINALLY, they came up with this feature. I've been begging for it for years). If you already have installed the independent driver it will guide you through changing its path.
 	 

     (iii) Always use custom build in Godot!

Bluetooth plugin notes:
-----------------------

### Android studio stuff:

- To import the Godot aar in android studio go to File -> Project Structure then Dependencies on the left hand side, then select the app then the + symbol under All Dependencies then select JAR/AAR, provide the absolute path to the library and select compileOnly.

- To get info from the log of connected device (Since godot debugger doesn't work) run `adb logcat godot:I ActivityManager:I InstantiateSingleton:I *:S`. 

### Godot stuff:

To use the plugin, add a Gdap file that has the following structure:

	[config]

	name="simplebluetooth"
	binary_type="local"
	binary="DemoLib-debug.aar"

	[dependencies]

	local=[]
	remote=[]
	custom_maven_repos=[] 

replacing the `binary` field with the aar in question. Save it as "*whatever*.gdap". Add this and the .aar file to res//android//plugins. 

In godot, the plugin should appear under Project -> Export (Android (Runnable)). Make sure to enable it and enable the following fields in the Permissons:

- Bluetooth
- Bluetooth Admin
- Bluetooth Priviledged

