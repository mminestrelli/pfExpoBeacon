import React, {Component} from 'react';
import {StyleSheet, Text, View, Switch, requireNativeComponent} from 'react-native';

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
    this.stopBeaconMonitoring();  
    this.setState({ buttonText: isOffText});
    this.setState({ isOn: false});
  }else {
    console.log("start");
    this.startBeaconMonitoring();
    this.setState({ buttonText: isOnText});
    this.setState({ isOn: true});
  }
  
}

async startBeaconMonitoring() {
  try {
    var {macAddress, distance} = await BeaconMonitor.startBeaconMonitoring();
    console.log("Mac Address: " + macAddress + " - Distance: " + distance);
  } catch (e) {
    console.error(e);
  }
}

async stopBeaconMonitoring() {
  try {
    var {macAddress, distance} = await BeaconMonitor.stopBeaconMonitoring();
    console.log("Mac Address: " + macAddress + " - Distance: " + distance);
  } catch (e) {
    console.error(e);
  }
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