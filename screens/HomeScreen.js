import * as WebBrowser from 'expo-web-browser';
import Modal from "react-native-modal";
import React from 'react';
import {
  Image,
  Button,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Switch,
  FlatList,
  ActivityIndicator,
  requireNativeComponent,
  ToastAndroid,
  DeviceEventEmitter
} from 'react-native';
import { MonoText } from '../components/StyledText';

var BeaconManager = require('NativeModules').BeaconManager;
const isOnText = "Switch OFF";
const isOffText = "Switch ON";

export default class HomeScreen extends React.Component {

  constructor(props){
    super(props);
    this._onStatusChange = this._onStatusChange.bind(this);
    this.state ={ isLoading: true}
    this.state = { isOn: false};
    this.state = { buttonText: isOffText};
    this.state = { isDataAvailable: false};
  }


  // Beacons stuff

  _onStatusChange = e => {
    if(this.state.isOn){
      console.log("stop");
      this.stopRangingBeacons();
      this.setState({ buttonText: isOffText});
      this.setState({ isOn: false});
    }else {
      console.log("start");
      this.startRangingBeacons();
      this.setState({ buttonText: isOnText});
      this.setState({ isOn: true});
    }
  }

  startRangingBeacons() {
    try {
      BeaconManager.startRangingBeacons();
      this.suscribeForEvents();
    } catch (e) {
      console.error(e);
    }
  }

  suscribeForEvents() {
    this.subscription = DeviceEventEmitter.addListener('didRangeBeaconsInRegion', (data) => {
      //TODO abrir pantalla con los beacons listados.
      //Podes usar este this.state.isDataAvailable y this.state.data para mostrar lista de beacons en el render().
      /* Vas a recibir esto:
      "beacons":[
           {
              "proximity":"immediate",
              "distance":0.01009895532367115,
              "uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6",
              "major":0,
              "minor":1,
              "rssi":-48,
              "macAddress":"0C:F3:EE:08:FC:DD"
           },
           {
              "proximity":"immediate",
              "distance":0.11185681527500883,
              "uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6",
              "major":0,
              "minor":1,
              "rssi":-49,
              "macAddress":"0C:F3:EE:04:19:21"
           }
      */
      this.state = { isDataAvailable: true};
      this.state = { data: data.beacons};
      ToastAndroid.show("Beacons: " + data.beacons[0].macAddress, ToastAndroid.SHORT);
    })
  }


  stopRangingBeacons() {
    try {
      BeaconManager.stopRangingBeacons();
      this.unsuscribeForEvents();
    } catch (e) {
      console.error(e);
    }
  }

  unsuscribeForEvents() {
    this.subscription.remove();
  }

  //

  componentDidMount(){
    return fetch('http://private-f63ff-standsv1.apiary-mock.com/stands')
      .then((response) => response.json())
      .then((responseJson) => {

        this.setState({
          isLoading: false,
          dataSource: responseJson,
        }, function(){

        });

      })
      .catch((error) =>{
        console.error(error);
      });
  }

  render(){
    let pic = {
    uri: 'https://upload.wikimedia.org/wikipedia/commons/d/de/Bananavarieties.jpg'
    };
    if(this.state.isLoading){
      return(
        <View style={{flex: 1, padding: 20}}>
          <ActivityIndicator/>
        </View>
      )
    }

    return (
      <View style={styles.container}>
        <ScrollView
          style={styles.container}
          contentContainerStyle={styles.contentContainer}>
          <View style={styles.welcomeContainer}>
            <Image
              source={
                __DEV__
                  ? require('../assets/images/robot-dev.png')
                  : require('../assets/images/robot-prod.png')
              }
              style={styles.welcomeImage}
            />
          </View>

          <View style={styles.getStartedContainer}>
            <DevelopmentModeNotice />

            <Text style={styles.getStartedText}>Get started by opening</Text>

            <View
              style={[styles.codeHighlightContainer, styles.homeScreenFilename]}>
              <MonoText>screens/HomeScreen.js</MonoText>
            </View>

            <Text style={styles.getStartedText}>
              Change this text and your app will automatically reload.
            </Text>
          </View>

          <View style={styles.top} >
            <Text>Monitor de Beacons esta: </Text>
            <Text>{this.state.isOn ? "Encendido" : "Apagado"}</Text>
            <Switch value={this.state.isOn} onValueChange={this._onStatusChange} />
          </View>

          <View style={styles.helpContainer}>
            <TouchableOpacity onPress={handleHelpPress} style={styles.helpLink}>
              <Text style={styles.helpLinkText}>
                Help, it didn’t automatically reload!
              </Text>
            </TouchableOpacity>
          </View>
          <View style={styles.beaconDetailContainer}>
            <Image source={pic} style={{width: 193, height: 110}}/>
            <Text style={styles.tabBarInfoText}>
              Title
            </Text>
            <Text style={styles.tabBarInfoText}>
              Description
            </Text>
          </View>

          <View style={{flex: 1, paddingTop:20}}>
            <FlatList
              data={this.state.dataSource}
              renderItem={({item}) => <View style={styles.beaconDetailContainer}>
                          <Image source={{uri:item.picture}} style={{width: 193, height: 110}}/>
                          <Text style={styles.tabBarInfoText}>
                            {item.title}
                          </Text>
                          <Text style={styles.tabBarInfoText}>
                            {item.description}
                          </Text>
                        </View>}
              keyExtractor={({id}, index) => id}
            />
          </View>
        </ScrollView>

      </View>
    );
  }
}


HomeScreen.navigationOptions = {
  header: null,
};

function DevelopmentModeNotice() {
  if (__DEV__) {
    const learnMoreButton = (
      <Text onPress={handleLearnMorePress} style={styles.helpLinkText}>
        Learn more
      </Text>
    );

    return (
      <Text style={styles.developmentModeText}>
        Development mode is enabled: your app will be slower but you can use
        useful development tools. {learnMoreButton}
      </Text>
    );
  } else {
    return (
      <Text style={styles.developmentModeText}>
        You are not in development mode: your app will run at full speed.
      </Text>
    );
  }
}

function handleLearnMorePress() {
  WebBrowser.openBrowserAsync(
    'https://docs.expo.io/versions/latest/workflow/development-mode/'
  );
}

function handleHelpPress() {
  WebBrowser.openBrowserAsync(
    'https://docs.expo.io/versions/latest/workflow/up-and-running/#cant-see-your-changes'
  );
}

function onPressWhereIAm(){
  WebBrowser.openBrowserAsync(
    'https://google.com'
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  developmentModeText: {
    marginBottom: 20,
    color: 'rgba(0,0,0,0.4)',
    fontSize: 14,
    lineHeight: 19,
    textAlign: 'center',
  },
  contentContainer: {
    paddingTop: 30,
  },
  welcomeContainer: {
    alignItems: 'center',
    marginTop: 10,
    marginBottom: 20,
  },
  welcomeImage: {
    width: 100,
    height: 80,
    resizeMode: 'contain',
    marginTop: 3,
    marginLeft: -10,
  },
  getStartedContainer: {
    alignItems: 'center',
    marginHorizontal: 50,
  },
  homeScreenFilename: {
    marginVertical: 7,
  },
  codeHighlightText: {
    color: 'rgba(96,100,109, 0.8)',
  },
  codeHighlightContainer: {
    backgroundColor: 'rgba(0,0,0,0.05)',
    borderRadius: 3,
    paddingHorizontal: 4,
  },
  getStartedText: {
    fontSize: 17,
    color: 'rgba(96,100,109, 1)',
    lineHeight: 24,
    textAlign: 'center',
  },
  tabBarInfoText: {
    fontSize: 17,
    color: 'rgba(96,100,109, 1)',
    textAlign: 'center',
  },
  navigationFilename: {
    marginTop: 5,
  },
  helpContainer: {
    marginTop: 15,
    alignItems: 'center',
  },
  top: {
  flex: 1,
  alignItems: "center",
  justifyContent: "center",
  },
  bottom: {
  flex: 1,
  alignItems: "center",
  justifyContent: "center",
  },
  beaconDetailContainer: {
    marginTop: 15,
    ...Platform.select({
      ios: {
        shadowColor: 'black',
        shadowOffset: { width: 0, height: -3 },
        shadowOpacity: 0.1,
        shadowRadius: 3,
      },
      android: {
        elevation: 20,
      },
    }),
    alignItems: 'center',
    backgroundColor: '#fbfbfb',
  },
  helpLink: {
    paddingVertical: 15,
  },
  helpLinkText: {
    fontSize: 14,
    color: '#2e78b7',
  },
});
