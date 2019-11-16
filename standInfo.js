import React, { Component } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import {Header, Rating} from 'react-native-elements';
import { SliderBox } from 'react-native-image-slider-box';

export default class StandInfo extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      images: [
        'https://source.unsplash.com/1024x768/?nature',
        'https://source.unsplash.com/1024x768/?water',
        'https://source.unsplash.com/1024x768/?girl',
        'https://source.unsplash.com/1024x768/?tree'
      ]
    };
  }

  render() {
    console.log(this.props.navigation.state.params.item.pictures);
    return (
      <View style={styles.container}>
         <View style={styles.top} >
           <Header
      leftComponent={{ icon: 'menu', color: '#fff' }}
      centerComponent={{ text: 'Stand Info', style: { color: '#fff' } }}
      rightComponent={{ icon: 'home', color: '#fff' }}
      />
           <SliderBox images={this.props.navigation.state.params.item.pictures} />
           <View style={styles.align}>
            <Text>Puntuación: </Text>
            <Rating
              imageSize={20}
              readonly
              startingValue={this.props.navigation.state.params.item.ranking}
            />
          </View>
            <Text style={styles.text, styles.title}>Descripción:</Text>
            <Text style={styles.text}>{this.props.navigation.state.params.item.description}</Text>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
   flex: 1,
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
  top: {
    flex: 1,
  },
  text: {
    alignItems: "center",
    justifyContent: "center",
    padding: 10,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  align : {
    flexDirection: 'row',
    justifyContent:'center',
    padding: 30,
  },
})
