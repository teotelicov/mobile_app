/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {AsyncStorage,RefreshControl, TextInput, Button, StyleSheet, Text, View, ScrollView, FlatList, AppRegistry, Picker } from 'react-native';
import { Repository } from "./repository.js";
import { StackNavigator } from 'react-navigation';
import { Product} from "./Product";
import * as Communications from 'react-native-communications';
import PopupDialog from 'react-native-popup-dialog';
import BarChart from "./BarChart";

class MainScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            refreshing: false,
            obj: null,
            categories: null,
            text: null,
            details: null,
            category: null,
            repository: new Repository()
        };
        this.onRefresh = this.onRefresh.bind(this);
        this.repository = new Repository();
    }

    static navigationOptions = {
        title: 'Welcome'
    };

    componentDidMount() {
        this.onRefresh();
    }

    onRefresh() {
        this.setState({refreshing: true});
        AsyncStorage.getAllKeys().then((value1) => {
            this.state.repository.products = [];
            for(let i = 0 ; i < value1.length; i++){
                AsyncStorage.getItem(value1[i]).then((value2) => {
                    console.log(value1[i]);
                    let p = JSON.parse(value2);
                    console.log(product);
                    // let off = null;
                    // for (let i = 0; i < this.state.repository.offers.length; ++i) {
                    //     if (this.state.repository.offers[i].product.name === product['name']) {
                    //         off = this.state.repository.offers[i];
                    //         break;
                    //     }
                    // }
                    let product = new Product(p['id'], p['name'], p['description']);
                    if (p['id'] + 1 > this.state.repository.ids) {
                        this.state.repository.ids = p['id'] + 1;
                    }
                    this.state.repository.products.push(product);
                }).done();
            }
        }).then(this.setState({refreshing: false})).done();
    }


    render() {
        if (this.state.refreshing) {
            return (<Text>Loading</Text>)
        }
        console.log(this.state.repository.products);
        let r_elems = this.state.repository.products;
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
                        refreshControl={<RefreshControl
                            refreshing={this.state.refreshing}
                            onRefresh={this.onRefresh}
                        />}
                        renderItem={({item}) =>
                            <View>
                                <Text
                                      onPress={() => navigate('Edit', {
                                          obj: item.value,
                                          deliveryService: this.state.repository.deliveryServices,
                                          refreshing: this.onRefresh,
                                          repo: this.state.repository,
                                      })}>{item.value.name}</Text>
                                <Text></Text>
                            </View>
                        }
                    />
                </ScrollView>
                <Button style={styles.emailbutton} onPress={() => navigate('SendEmail')} title="Send E-mail" />
                <Button style={styles.addButton}
                        onPress={() => {
                            navigate('Edit', {
                                obj: null,
                                deliveryService: this.state.repository.deliveryServices,
                                repo: this.state.repository,
                                refreshing: this.onRefresh})

                        }} title={'Add Product'}/>

                <Button style={styles.addButton}
                        onPress={() => {
                            navigate('AddOffer', {
                                obj: null,
                                repo: this.state.repository,
                                deliveryService: this.state.repository.deliveryServices,
                                refreshing: this.onRefresh})

                        }} title={'Add Offer'}/>
                <Button onPress={() => this.onRefresh()} title={"Offers"}/>
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
        if (state.params.obj) {
            this.state = {
                name: state.params.obj.name,
                description: state.params.obj.description,
                repo: state.params.repo
            };
            this.chart = {
                values: [[1,2,3,1,2,3,5,3,4,5]],
                colors: {
                    labelsColor : ['#4286F5', '#DC4437', '#F5B400'],
                    axisColor : 'rgba(216, 216, 216, 1)',
                },
                labels: this.getProductNames(),
                selected: 2,
                axis: this.getProductNames(),
            };
        }
        else {
            this.state = {
                name: '',
                description: '',
                repo: state.params.repo
            }
            this.chart = {
                values: [[1,2,3]],
                colors: {
                    labelsColor : ['#4286F5', '#DC4437', '#F5B400'],
                    axisColor : 'rgba(216, 216, 216, 1)',
                },
                labels: ["ala","bala","portocala"],
                selected: 2,
                axis: ["ala","bala","portocala"],
            };
        }
    }

    render() {

        //console.log(this.state.repository.offers);
        let r_elems = this.state.repo.deliveryServices;
        let datas = [];
        for (let i = 0; i < r_elems.length; i++) {
            // if(r_elems[i].product.name === this.state.name)
            datas.push({key: i, value: r_elems[i]});
        }

        const {state} = this.props.navigation;
        const {goBack} = this.props.navigation;
        return (
            <ScrollView>

                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({name: text})}
                    value={this.state.name}
                />
                <TextInput
                    style={{marginTop: "5%", width: "90%",borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({description: text})}
                    value={this.state.description}
                />
                <PopupDialog ref={(popupDialog => {this.popupDialog = popupDialog;})}>
                    <Text>
                        Please insert all fields!
                    </Text>
                </PopupDialog>
                <Button onPress={() => {
                    if (this.state.name === '' || this.state.description === '') {
                        this.popupDialog.show();
                        return;
                    }
                    if (state.params.obj) {
                        AsyncStorage.setItem('@megaphone:' + String(state.params.obj.id), JSON.stringify(
                                {
                                    id: state.params.obj.id,
                                    name: this.state.name,
                                    description: this.state.description
                                })).then(state.params.refreshing()).then(goBack(null)).done();
                    } else {

                        AsyncStorage.setItem('@megaphone:' + String(this.state.repo.ids), JSON.stringify(
                            {
                                id: this.state.repo.ids,
                                name: this.state.name,
                                description: this.state.description
                            })).then(state.params.refreshing()).then(goBack(null)).done();
                            this.state.repo.ids += 1;
                            //goBack(null);
                        }


                }} title={"Update"}/>
                <Button onPress={() => {
                    if (state.params.obj) {
                        let key = ['@megaphone:' + String(state.params.obj.id)];
                        AsyncStorage.multiRemove(key).then(state.params.refreshing()).then(goBack(null)).done();
                    }
                }} title={"Delete"}/>

                <Picker
                    style={{marginTop: "5%", width: "90%",borderWidth: 1, backgroundColor: 'white'}}
                    onValueChange={(itemValue, itemIndex) => {
                        this.setState({deliveryService: itemValue});
                    }}
                >
                    {state.params.deliveryService.map((item, index) => <Picker.Item key={item.name} label={item.name} value={item.name} />)}
                </Picker>

                <BarChart height={180} chart={this.chart} />


            </ScrollView>

        );
    }
    getProductNames() {
        const { params } = this.props.navigation.state;
        let names = [];
        for(let i = 0; i < params.repo.products.length; ++i) {
            names = names.concat(params.repo.products[i].name);
        }
        console.log(names);
        return names;
    }
}

class AddOfferScreen extends React.Component {
    constructor(props) {
        super(props);
        const {state} = this.props.navigation;
        if (state.params.obj) {
            this.state = {
                name: state.params.obj.name,
                description: state.params.obj.description,

            };
        }
        else {
            this.state = {
                name: '',
                description: '',
                repo: state.params.repo
            }
        }
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
                    onChangeText={(text) => this.setState({description: text})}
                    value={this.state.description}
                />
                <PopupDialog ref={(popupDialog => {this.popupDialog = popupDialog;})}>
                    <Text>
                        Please insert all fields!
                    </Text>
                </PopupDialog>
                <Button onPress={() => {
                    if (this.state.name === '' || this.state.description === '') {
                        this.popupDialog.show();
                        return;
                    }
                    if (state.params.obj) {
                        AsyncStorage.setItem('@megaphone:' + String(state.params.obj.id), JSON.stringify(
                            {
                                id: state.params.obj.id,
                                name: this.state.name,
                                description: this.state.description
                            })).then(state.params.refreshing()).then(goBack(null)).done();
                    } else {

                        AsyncStorage.setItem('@megaphone:' + String(this.state.repo.ids), JSON.stringify(
                            {
                                id: this.state.repo.ids,
                                name: this.state.name,
                                description: this.state.description
                            })).then(state.params.refreshing()).then(goBack(null)).done();
                        this.state.repo.ids += 1;
                        //goBack(null);
                    }


                }} title={"Update"}/>
                <Button onPress={() => {
                    if (state.params.obj) {
                        let key = ['@megaphone:' + String(state.params.obj.id)];
                        AsyncStorage.multiRemove(key).then(state.params.refreshing()).then(goBack(null)).done();
                    }
                }} title={"Delete"}/>
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
