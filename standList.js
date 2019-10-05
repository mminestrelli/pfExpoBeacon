import React, { Component } from 'react';
import { FlatList, StyleSheet, Text, View } from 'react-native';

/**
* @param props properties needed to render StandList:
* - stands: array of stands' data.
*/
export default class StandList extends React.Component {

  render() {
    return (
      <View style={styles.container}>
        <FlatList
          data={this.props.stands}
          renderItem={({item}) => <Text style={styles.item}>{item.macAddress}</Text>}
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
