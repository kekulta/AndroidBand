# AndroidBand

AndroidBand is a simple music creation app that allows you to merge different audio tracks into one single track, providing a seamless and intuitive experience for music enthusiasts and creators.

![App Overview Screenshot](https://github.com/kekulta/AndroidBand/assets/33986203/6495de2f-d2cf-4872-9fa7-ffa2c4c97fd0)

*AndroidBand with added samples*

## Features

### QuickSound and Sound Library

At the very start of your journey with AndroidBand, you'll notice five buttons at the top of the screen. Four of these are quick sound buttons, each representing a different category of sounds (one of which is currently inactive), and the fifth is the Sound Library button.

![QuickSound Screenshot](https://github.com/kekulta/AndroidBand/assets/33986203/573588c4-23aa-4fd8-9f55-f1557e6db508)
*QuickSound Screenshot*

By clicking on the Melody, Drums, or FX QuickSound buttons, you can swiftly add a sample card of the respective category to your workspace. This workspace is where you'll be able to mix and match sounds to create your unique piece of music. Each of these sample cards comes with sliders, allowing you to adjust the volume or speed of the sample to your liking. 

Each sample card also comes equipped with three control buttons: 'Seek to Start', 'Play/Pause', and 'Mute'. An additional menu button provides two options: rename (which can also be done with a long tap on the sample name) or delete the sample.

A long tap on any QuickSound button will take you to the relevant section of the Sound Library, which is a comprehensive collection of sounds that you can use in your tracks. You can also access this library through the Sound Library button, which will guide you to the first section (i.e., Melodies).

The fourth section of the library is currently empty; this is a dedicated space for your recordings. Here, you can store, access, and manage all your recorded tracks.

### Adding Sounds

To add a sound to your workspace, simply press the plus button next to a sound. This will add the sound to your workspace as a sample, ready to be used in your music. 

![SoundsLibrary](https://github.com/kekulta/AndroidBand/assets/33986203/6587efc6-6fb5-49dd-9689-bcdab639705f)
*SoundsLibrary, white circle marks that Melody One choosed as a quick sound*

For quick access, a long press on the plus button will select this sound as a quick sound in the relevant section. You can also set it as a quick sound from the menu. Note that other options in this menu are available only for recordings.

Once you've added the sounds you like to your workspace and tuned them to your satisfaction, you're ready to start recording.

### Recording

Recording is implemented through audio capturing, which requires explicit permission from the user. This is to ensure your privacy and control over the app's access to your device's capabilities. 

![Recording Screenshot](https://github.com/kekulta/AndroidBand/assets/33986203/74c0f4d9-496a-4d6b-aa59-732638cfb76b)
*Recording Control Panel. Help button, start/stop button and next mode button*

You can start and stop recording with the button at the center of the bottom panel. The Help button on the left provides information about capturing, and the button on the right switches to the next mode (you can also swipe to switch).

When the recording is finished, you'll receive a message with the name of the recorded track. You can then go to the Sound Library, where you can use the menu button to rename, share, or delete the track.

### Mic Recorder Mode

If you return to the workspace and press the next mode button or swipe, you'll enter the Mic Recorder mode. 

In this mode, you can start or stop recording from your phone's mic with the center button. This is a great way to add personal touches or unique sounds to your tracks. These recordings will also be added to the library, allowing you to use them in future tracks.

### Sequencer Mode

The third and last mode is Sequencer mode. 

![Sequencer Mode Screenshot](https://github.com/kekulta/AndroidBand/assets/33986203/97a3eca5-963f-4de7-ad46-060bd6ab949b)
*Sequncer control panel. Start/Stop recording, Start/Stop playing and render buttons. Last two inactive without previously made record*

In this mode, you can change the speed or volume of the samples while recording. AndroidBand will remember your interactions with the samples and repeat them when you click the Play button. The Render button will merge your samples into one audio file and add it to the library. This allows you to create complex tracks with layered sounds and effects.

### User Experience

We care deeply about your user experience. That's why, before requesting any permissions, we'll show a dialog with explanations. This is to ensure that you are fully informed about why we need certain permissions and what we do with the data.

<img width="154" src=https://github.com/kekulta/AndroidBand/assets/33986203/8a396c51-d348-43de-95b1-5bd4ba5c3c47>
<img width="154" src=https://github.com/kekulta/AndroidBand/assets/33986203/3819b28e-fc02-44a5-9d74-a801f01c908f>
<img width="154" src=https://github.com/kekulta/AndroidBand/assets/33986203/2354c3c7-dc2b-42eb-9611-df295d692e80>

### Adapted Layout

The workspace has an adapted layout for both portrait and landscape orientations. This allows you to work in the way that's most comfortable for you, and ensures that the app is as easy to use as possible on a variety of devices and screen sizes.

### Themes

We understand that aesthetics matter, and that's why we offer both dark and light themes. You can choose the one that suits your style or switch depending on your environment or mood.

### Languages

To make AndroidBand accessible to users around the world, we support both English and Russian languages. We're working on adding more languages to this list.

## Conclusion

AndroidBand is a small app for creating and merging music tracks. With its intuitive interface, extensive sound library, and powerful recording and sequencing features, it provides a platform for you to unleash your creativity and make your own unique music.
