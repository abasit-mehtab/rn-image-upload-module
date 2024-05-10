import * as React from 'react';

import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
  Image,
  ToastAndroid,
} from 'react-native';
import * as ImagePicker from 'react-native-image-picker';
import {
  multiply,
  uploadImage,
  requestStoragePermission,
} from 'rn-image-upload-module';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [imageSelected, setImageSelcted] = React.useState<boolean>(false);
  const [imageData, setImageData] = React.useState(null);

  const onGalleryPress = React.useCallback(async () => {
    try {
      await requestStoragePermission();

      const options: ImagePicker.ImageLibraryOptions = {
        selectionLimit: 1,
        mediaType: 'photo',
      };

      ImagePicker.launchImageLibrary(options, (res) => {
        if (res.didCancel) {
          console.log('User cancelled image picker');
        } else if (res.errorCode) {
          console.log('ImagePicker Error: ', res.errorMessage);
        } else {
          console.log(
            'ImagePicker response ==>> ',
            JSON.stringify(res, null, 2)
          );

          if (res) {
            setImageSelcted(true);
            setImageData(res?.assets[0]);
          }
        }
      });
    } catch (error) {
      console.log('Error requesting storage permission ==>> ', error);
    }
  }, []);

  const onCameraPress = React.useCallback(async () => {
    try {
      await requestStoragePermission();

      const options = {
        saveToPhotos: false,
        mediaType: 'photo',
      };

      ImagePicker.launchCamera(options, (res) => {
        if (res.didCancel) {
          console.log('User cancelled image picker');
        } else if (res.errorCode) {
          console.log('ImagePicker Error: ', res.errorMessage);
        } else {
          console.log(
            'ImagePicker response ==>> ',
            JSON.stringify(res, null, 2)
          );
          if (res) {
            setImageSelcted(true);
            setImageData(res?.assets[0]);
          }
        }
      });
    } catch (error) {
      console.log('Error requesting storage permission ==>> ', error);
    }
  }, []);

  const onUploadImage = async () => {
    try {
      if (imageData == null) {
        ToastAndroid.show(
          'Please select or take a picture first!',
          ToastAndroid.SHORT
        );
        return;
      }
      const resp = await uploadImage(imageData);

      console.log('Response from uploadImage native function ==>> ', resp);
    } catch (error) {
      console.log('Error in uploading image ==>> ', error);
      const errorMessage = error?.toString();
      ToastAndroid.show(errorMessage, ToastAndroid.SHORT);
    }
  };

  const onClearPress = () => {
    setImageData(null);
  };

  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  React.useEffect(() => {
    console.log('result ==>> ', result);
    console.log('imageSelected ==>> ', imageSelected);
  }, [result, imageSelected]);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.subContainer}>
        <View style={styles.imageBox}>
          {imageData != null ? (
            <Image
              source={{ uri: imageData?.uri }}
              style={styles.imageStyle}
              resizeMode="contain"
            />
          ) : (
            <Text style={styles.textStyle}>No Image!</Text>
          )}
        </View>

        <View style={styles.buttonsContainer}>
          <TouchableOpacity
            onPress={() => onGalleryPress()}
            style={styles.buttonStyle}
          >
            <Text style={styles.buttonTextStyle}>Select from Gallery</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => onCameraPress()}
            style={styles.buttonStyle}
          >
            <Text style={styles.buttonTextStyle}>Take from Camera</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => onUploadImage()}
            style={styles.smallButtonStyle}
          >
            <Text style={styles.buttonTextStyle}>Upload</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => onClearPress()}
            style={styles.smallButtonStyle}
          >
            <Text style={styles.buttonTextStyle}>Clear</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  subContainer: {
    paddingVertical: 50,
  },
  imageBox: {
    width: 250,
    height: 250,
    borderColor: '#000000',
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 30,
  },
  textStyle: {
    color: '#000000',
    fontSize: 16,
  },
  buttonsContainer: {
    alignItems: 'center',
  },
  buttonStyle: {
    backgroundColor: '#0d1e8c',
    width: 160,
    height: 40,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 15,
  },
  smallButtonStyle: {
    backgroundColor: '#0d1e8c',
    width: 90,
    height: 40,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 15,
  },
  buttonTextStyle: {
    color: '#FFFFFF',
  },
  imageStyle: {
    width: '100%',
    height: '100%',
  },
});
