import React, {Component} from 'react';
import {StyleSheet, Text, View, Switch, requireNativeComponent, ToastAndroid, DeviceEventEmitter} from 'react-native';

var BeaconMonitor = require('NativeModules').BeaconMonitor;
const isOnText = "Switch OFF";
const isOffText = "Switch ON";

export default class App extends Component {

constructor(props) {
  super(props);
  this._onStatusChange = this._onStatusChange.bind(this);
  this.state = { isOn: false};
  this.state = { buttonText: isOffText};
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
    BeaconMonitor.startRangingBeacons();
    this.suscribeForEvents();
  } catch (e) {
    console.error(e);
  }
}

suscribeForEvents() {
  this.subscription = DeviceEventEmitter.addListener('didRangeBeaconsInRegion', (data) => {
    //TODO abrir pantalla con los beacons listados. 
    //Ojo, porque recibir estos eventos deberían refreshear la data de la lista. Ver como resolver el ciclo de vida.
    ToastAndroid.show("Mac Address: " + data.macAddress + " - Distance: " + data.distance, ToastAndroid.SHORT);
  })  
}


stopRangingBeacons() {
  try {
    BeaconMonitor.stopRangingBeacons();
    this.unsuscribeForEvents();
  } catch (e) {
    console.error(e);
  }
}

unsuscribeForEvents() {
  this.subscription.remove();  
}

render() {
 return (
   <View style={styles.container}>
     <View style={styles.top} >
      <Text>Monitor de Beacons esta: </Text>
      <Text>{this.state.isOn ? "Encendido" : "Apagado"}</Text>
      <Switch value={this.state.isOn} onValueChange={this._onStatusChange} />
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
alignItems: "center",
justifyContent: "center",
},
bottom: {
flex: 1,
alignItems: "center",
justifyContent: "center",
},
});