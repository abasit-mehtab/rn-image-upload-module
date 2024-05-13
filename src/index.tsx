import { NativeModules, Platform, PermissionsAndroid } from 'react-native';

async function requestStoragePermission() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      {
        title: 'Storage Permission',
        message: 'Your app needs access to storage',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('Storage permission granted');
    } else {
      console.log('Storage permission denied');
    }
  } catch (err) {
    console.log(err);
  }
}

const LINKING_ERROR =
  `The package 'rn-image-upload-module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RnImageUploadModule = NativeModules.RnImageUploadModule
  ? NativeModules.RnImageUploadModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return RnImageUploadModule.multiply(a, b);
}

export function uploadBase64Image(base64Image: string): Promise<string> {
  if (!RnImageUploadModule.uploadBase64Image) {
    throw new Error('Image upload function is not available');
  }
  return RnImageUploadModule.uploadBase64Image(base64Image);
}

export { requestStoragePermission };
