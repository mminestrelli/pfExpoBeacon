import React, { Component } from 'react';
import {
  StyleSheet, View, ToastAndroid, DeviceEventEmitter
} from 'react-native';
import { Button, Header } from 'react-native-elements';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import StandList from './standList';
import StandInfo from './standInfo';

const { BeaconManager } = require('NativeModules');


class App extends Component {
  constructor(props) {
    super(props);
    this.state = { isLoading: true };
    // TODO: Figure out how to correctly initialize prop
    this.state = { data: [{}] };
    this.state = { dataSource: [{}] };
  }

  // Lifecycle events
  componentDidMount() {
    this.getAllStands();
  }

  componentDidUpdate(prevProps, prevState) {
    if ((prevState.data !== this.state.data) && this.state.data !== undefined) {
      this.getOrderedStands();
    }
  }

  componentWillUnmount() {
    this.startSubscription.remove();
    this.stopSubscription.remove();
  }

  // Services TODO: Modularize
  getAllStands() {
    return fetch('http://private-f63ff-standsv1.apiary-mock.com/stands')
      .then((response) => response.json())
      .then((responseJson) => {
        this.setState({
          isLoading: false,
          dataSource: responseJson,
        }, () => {
        });
      })
      .catch((error) => {
        console.error(error);
      });
  }

  getOrderedStands() {
    return fetch(`http://private-f63ff-standsv1.apiary-mock.com/stands/${this.state.data[0].macAddress}`)
      .then((response) => response.json())
      .then((responseJson) => {
        console.log(responseJson);
        this.setState({
          isLoading: false,
          dataSource: responseJson,
        }, () => {

        });
      })
      .catch((error) => {
        console.error(error);
      });
  }

  // Beacons
  onRangeButtonPress = () => {
    this.startRangingBeacons();
    this.setState({ isLoading: true });
  }

  suscribeForEvents() {
    this.startSubscription = DeviceEventEmitter
      .addListener(BeaconManager.EVENT_BEACONS_RANGED, (data) => {
        // TODO abrir pantalla con los beacons listados.
        // Podes usar este this.state.isDataAvailable y
        // this.state.data para mostrar lista de beacons en el render().
        /* Vas a recibir esto:
    "beacons":[
         {
            "proximity":"immediate",
            "distance":0.01009895532367115,
            "uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6",
            "major":0,
            "minor":1,
            "rssi":-48,
            "macAddress":"0C:F3:EE:08:FC:DD"
         },
         {
            "proximity":"immediate",
            "distance":0.11185681527500883,
            "uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6",
            "major":0,
            "minor":1,
            "rssi":-49,
            "macAddress":"0C:F3:EE:04:19:21"
         }
    */
        if (data.beacons) {
          this.stopRangingBeacons();
          ToastAndroid.show(`Beacons: ${data.beacons[0].macAddress}`, ToastAndroid.SHORT);
          this.setState({
            data: data.beacons
          }, () => {
          });
        }
      });
  }

  startRangingBeacons() {
    try {
      BeaconManager.startRangingBeacons();
      this.suscribeForEvents();
    } catch (e) {
      console.log('Should include logger');
    }
  }

  stopRangingBeacons() {
    this.setState({ isLoading: false });
    try {
      BeaconManager.stopRangingBeacons();
      this.unsuscribeForEvents();
    } catch (e) {
      console.error(e);
    }
  }

  unsuscribeForEvents() {
    this.stopSubscription = DeviceEventEmitter
      .addListener(BeaconManager.EVENT_BEACONS_RANGE_STOPPED, () => {
        ToastAndroid.show('Beacons range stopped', ToastAndroid.SHORT);
        this.startSubscription.remove();
      });
  }

  // Rendering and Screen UI events handlers

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.top}>
          <Header
            leftComponent={{ icon: 'menu', color: '#fff' }}
            centerComponent={{ text: 'EXPO ITBA', style: { color: '#fff' } }}
            rightComponent={{ icon: 'home', color: '#fff' }}
          />
          <Button
            title="Range"
            onPress={this.onRangeButtonPress}
            loading={this.state.isLoading}
          />
          <StandList
            stands={this.state.dataSource}
            navigation={this.props.navigation}
            isLoadingList={this.state.isLoading}
          />
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
  },
  bottom: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

const MainNavigator = createStackNavigator({
  App: { screen: App },
  StandInfo: { screen: StandInfo },
},
{
  defaultNavigationOptions: {
    header: null,
  }
});

const AppNavigation = createAppContainer(MainNavigator);

export default AppNavigation;
