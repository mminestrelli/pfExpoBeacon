import React, {Component} from 'react';
import {StyleSheet, Text, View, requireNativeComponent} from 'react-native';

const Bulb = requireNativeComponent("Bulb")
var ToastExample = require('NativeModules').ToastExample;
var BeaconMonitor = require('NativeModules').BeaconMonitor;

export default class App extends Component {

constructor(props) {
  super(props);
  this._onStatusChange = this._onStatusChange.bind(this);
  this.state = { isOn: false };
}
_onStatusChange = e => {
  this.setState({ isOn: e.nativeEvent.isOn});
  if(this.state.isOn){
    BeaconMonitor.startBeaconMonitoring();  
  }else {
    BeaconMonitor.stopBeaconMonitoring();  
  }
  
}

render() {
 return (
   <View style={styles.container}>
     <View style={styles.top} >
      <Text>Monitor de Beacons esta: </Text>
      <Text>{this.state.isOn ? "Encendido" : "Apagado"}</Text>
    </View>
    <Bulb style={ styles.bottom }  isOn={this.state.isOn} onStatusChange={this._onStatusChange} />
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