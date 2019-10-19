import React, { Component } from 'react';
import { FlatList, StyleSheet, Text, View, Button } from 'react-native';
import StandInfo from './standInfo';

/**
* @param props properties needed to render StandList:
* - stands: array of stands' data.
*/
export default class StandList extends React.Component {

  render() {
    const { navigate } = this.props.navigation;
    return (
      <View style={styles.container}>
        <FlatList
          data={this.props.stands}
          renderItem={({item}) => <Button title = {item.macAddress} onPress= {() => navigate('StandInfo', { 
            macAddress : item.macAddress })}/>}
            keyExtractor={(item, index) => index.toString()}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
   flex: 1,
   paddingTop: 22
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
})
