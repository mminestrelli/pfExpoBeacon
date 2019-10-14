import React, {Component} from 'react';
import {StyleSheet, Text, View, Switch, requireNativeComponent, ToastAndroid, DeviceEventEmitter} from 'react-native';
import StandList from './standList';

var BeaconManager = require('NativeModules').BeaconManager;
const isOnText = "Switch OFF";
const isOffText = "Switch ON";

export default class App extends Component {

constructor(props) {
  super(props);
  this._onStatusChange = this._onStatusChange.bind(this);
  this.state = { isOn: false};
  this.state = { buttonText: isOffText};
  this.state = { isDataAvailable: false};
  this.state = { data: [
            {macAddress: '0C:F3:EE:04:18:A0'},
            {macAddress: 'Dan'},
            {macAddress: 'Dominic'},
            {macAddress: 'Jackson'},
            {macAddress: 'James'},
            {macAddress: 'Joel'},
            {macAddress: 'John'},
            {macAddress: 'Jillian'},
            {macAddress: 'Jimmy'},
            {macAddress: 'Julie'},
          ]};
}

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
  this.startSubscription = DeviceEventEmitter.addListener(BeaconManager.EVENT_BEACONS_RANGED, (data) => {
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
    this.setState({
      isDataAvailable: true,
      data: data.beacons
    });
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
  this.stopSubscription = DeviceEventEmitter.addListener(BeaconManager.EVENT_BEACONS_RANGE_STOPPED, () => {
    ToastAndroid.show("Beacons range stopped", ToastAndroid.SHORT);
    this.startSubscription.remove();
  })
}

componentWillUnmount() {
  this.startSubscription.remove();
  this.stopSubscription.remove();
}

render() {
 return (
   <View style={styles.container}>
      <View style={styles.top} >
        <Text>Monitor de Beacons esta: </Text>
        <Text>{this.state.isOn ? "Encendido" : "Apagado"}</Text>
        <Switch value={this.state.isOn} onValueChange={this._onStatusChange} />
        <StandList stands={this.state.data}/>
      </View>
  </View>
);
}
}

const styles = StyleSheet.create({
container: {
flex: 1,
backgroundColor: '#F5FCFF',
},
top: {
flex: 1,
},
bottom: {
flex: 1,
alignItems: "center",
justifyContent: "center",
},
});
