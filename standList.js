import React, { Component } from 'react';
import { FlatList, StyleSheet, Text, View,Image,ScrollView } from 'react-native';
import { ListItem } from 'react-native-elements'

/**
* @param props properties needed to render StandList:
* - stands: array of stands' data.
*/
export default class StandList extends React.Component {
  constructor(props){
  super(props);
  this.state ={ isLoading: true};
  this.state = { isDataAvailable: false};
}
  componentDidMount(){
    this.getStands();
  }
  getStands(){
    console.log('---------ESTO NO SON PATRANAS----------'+'http://private-f63ff-standsv1.apiary-mock.com/stands/'+this.props.stands[0].macAddress);
    return fetch('http://private-f63ff-standsv1.apiary-mock.com/stands/'+this.props.stands[0].macAddress)
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
  keyExtractor = (item, index) => index.toString()

  renderItem = ({ item }) => (
  <ListItem
    title={item.title}
    subtitle={item.description}
    leftAvatar={{
      source: item.picture && { uri: item.picture }
    }}
    bottomDivider
    chevron
  />
  )

  render () {
  return (
    <View style={styles.container}>
    <ScrollView
        style={styles.container}
        contentContainerStyle={styles.contentContainer}>
    <View style={styles.container}>
      <FlatList
        data={this.props.stands}
        renderItem={({item}) => <Text style={styles.item}>{item.macAddress}</Text>}
      />
    </View>
    <FlatList
      keyExtractor={this.keyExtractor}
      data={this.state.dataSource}
      renderItem={this.renderItem}
    />
    </ScrollView>
  </View>
  )
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
})
