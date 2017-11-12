/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import { TextInput, Button, StyleSheet, Text, View, ScrollView, FlatList, AppRegistry, Picker } from 'react-native';
import { Repository } from "./repository.js";
import { StackNavigator } from 'react-navigation';
import * as Communications from 'react-native-communications';

class MainScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            refreshing: false,
        };
        this.onRefresh = this.onRefresh.bind(this);
        this.repository = new Repository();
    }

    static navigationOptions = {
        title: 'Welcome'
    };

    onRefresh() {
        this.setState({refreshing: true});
        this.setState({refreshing: false});
    }

    render() {
        let r_elems = this.repository.deliveryServices;
        let datas = [];
        for (let i = 0; i < r_elems.length; i++) {
            datas.push({key: i, value: r_elems[i]});
        }
        const { navigate } = this.props.navigation;
        return (
            <View>
              <ScrollView>
                <FlatList
                    data={datas}
                    renderItem={({item}) =>
                        <View>
                          <Text
                                onPress={() => navigate('Edit', {
                                    obj: item.value,
                                    products: this.repository.products,
                                    refreshing: this.onRefresh,
                                })}>{item.value.title}</Text>
                          <Text
                                onPress={() => navigate('Edit', {
                                    obj: item.value,
                                    products: this.repository.products,
                                    refreshing: this.onRefresh,
                                })}>{item.value.details}</Text>
                          <Text
                                onPress={() => navigate('Edit', {
                                    obj: item.value,
                                    products: this.repository.products,
                                    refreshing: this.onRefresh,
                                })}>{item.value.product.name}</Text>
                          <Text></Text>
                        </View>
                    }
                />
              </ScrollView>
                <Button style={styles.emailbutton} onPress={() => navigate('SendEmail')} title="Send E-mail" />
            </View>
        );
    }
}
class SendMailScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            email: '',
            subject: '',
            content: '',
        }
    }

    render() {
        return (
            <View style={{alignItems: "center"}}>
                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({email: text})}
                    placeholder={"To"}
                />
                <TextInput
                    style={{marginTop: "5%", width: "90%",borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({subject: text})}
                    placeholder={"Title"}
                />
                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({content: text})}
                    placeholder={"Content"}
                />
                <Button onPress={() => Communications.email([this.state.email, this.state.email], null, null, this.state.subject, this.state.content)} title={"Send"}/>
            </View>
        );
    }
}

class EditScreen extends React.Component {
    constructor(props) {
        super(props);
        const {state} = this.props.navigation;
        this.state = {
            name: state.params.obj.name,
            address: state.params.obj.address,
            product: state.params.obj.product.name,
        };
    }

    render() {
        const {state} = this.props.navigation;
        const {goBack} = this.props.navigation;
        return (
            <View style={{alignItems: "center"}}>
                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({name: text})}
                    value={this.state.name}
                />
                <TextInput
                    style={{marginTop: "5%", width: "90%",borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({address: text})}
                    value={this.state.address}
                />
                <Picker
                    style={{marginTop: "5%", width: "90%",borderWidth: 1, backgroundColor: 'white'}}
                    selectedValue={this.state.product}
                    onValueChange={(itemValue, itemIndex) => {
                        this.setState({product: itemValue});
                    }}
                >
                    {state.params.products.map((item, index) => <Picker.Item key={item.name} label={item.name} value={item.name} />)}
                </Picker>
                <Button onPress={() => {
                    let found = false;
                    let prod = null;
                    for (let i = 0; i < state.params.products.length; i++) {
                        if (this.state.product === state.params.products[i].name) {
                            found = true;
                            prod= state.params.products[i];
                        }
                    }
                    if (found) {
                        state.params.obj.name = this.state.name;
                        state.params.obj.address = this.state.address;
                        state.params.obj.product = prod;
                        state.params.refreshing();
                        goBack(null);
                    }

                }} title={"Save"}/>
            </View>

        );
    }
}

const NavigApp = StackNavigator({
    Home: { screen: MainScreen },
    SendEmail: { screen: SendMailScreen},
    Edit: {screen: EditScreen}
});

export default class App extends React.Component{
    render() {
        return <NavigApp />;
    }
};
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
