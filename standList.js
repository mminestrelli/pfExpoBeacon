import React from 'react';
import {
  FlatList, StyleSheet, View, ScrollView, ActivityIndicator
} from 'react-native';
import { ListItem } from 'react-native-elements';

/**
* @param props properties needed to render StandList:
* - stands: array of stands' data.
* - isLoadingList: bool to show if list is loading.
*/
export default class StandList extends React.Component {
  keyExtractor = (item, index) => index.toString()

  renderItem = ({ item }) => (
    <ListItem
      title={item.title}
      subtitle={item.short_description}
      leftAvatar={{
        source: item.cover_url && { uri: item.cover_url }
      }}
      onPress={() => this.props.navigation.navigate('StandInfo', { item })}
      bottomDivider
      chevron
    />
  )

  render() {
    if (this.props.isLoadingList) {
      return (
        <View style={{ flex: 1, padding: 20 }}>
          <ActivityIndicator />
        </View>
      );
    }
    return (
      <View style={styles.container}>
        <ScrollView
          style={styles.container}
          contentContainerStyle={styles.contentContainer}
        >
          <FlatList
            keyExtractor={this.keyExtractor}
            data={this.props.stands}
            renderItem={this.renderItem}
          />
        </ScrollView>
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
  tabBarInfoText: {
    fontSize: 17,
    color: 'rgba(96,100,109, 1)',
    textAlign: 'center',
  },
  beaconDetailContainer: {
    marginTop: 15,
    alignItems: 'center',
    backgroundColor: '#fbfbfb',
  },
});
