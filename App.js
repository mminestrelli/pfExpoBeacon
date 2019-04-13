import React, {Component} from 'react';
import {StyleSheet, Text, View, requireNativeComponent} from 'react-native';
const Bulb = requireNativeComponent("Bulb")
export default class App extends Component {
render() {
 return (
   <View style={styles.container}>
     <View style={styles.top} />
     <Bulb style={ styles.bottom } isOn={true}/>
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