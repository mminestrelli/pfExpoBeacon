import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

export default class StandInfo extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Text> INFO </Text>
        <Text>
          {' '}
          {this.props.navigation.getParam('macAddress', 'default value') }
          {' '}
        </Text>
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
});
