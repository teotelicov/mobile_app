/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
    Button,
    ListView,
    Platform,
    StyleSheet,
    Text,
    TouchableHighlight,
    AsyncStorage,
    View,
    ScrollView,
    TextInput,
    Picker,

} from 'react-native';
import { StackNavigator } from 'react-navigation';
import PopupDialog from "react-native-popup-dialog/src/PopupDialog";

const offersList = [
    {
        id: 1,
        productId:1,
        deliveryId:1,
        price:15,
        date:"11/12/2017"
    },
    {
        id: 2,
        productId:1,
        deliveryId:2,
        price:17,
        date:"11/12/2017"
    }
    ,
    {
        id: 3,
        productId:2,
        deliveryId:1,
        price:20,
        date:"15/12/2017"
    }
];
productList = [
    {
        id: 1,
        name: 'Pizza Capricciosa',
        description: 'Description1'
    },
    {
        id: 2,
        name: 'Pizza Diavola',
        description: 'Description2'
    },
    {
        id: 3,
        name: 'Pizza Primavera',
        description: 'Description3'
    }
];
deliveryList = [
    {
        id: 1,
        name: 'Pizza Hut',
        address: 'Address1'
    },
    {
        id: 2,
        name: 'Pizza Grande',
        address: 'Address2'
    },
    {
        id: 3,
        name: 'Pizza Venezia',
        address: 'Address3'
    }
];


export class MainScreen extends React.Component {
    static navigationOptions = {
        title: 'Delivery Services',
    };
    constructor(props) {
        super(props);
        this.products = [];
        this.deliveries = [];
        this.name ="";
        this.deliveries="";
        this.state = {dataSourceProducts: [],dataSourceOffers: [], offers: []};
        this.ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.loadData();
    }

    componentWillMount() {
        this.loadData();
    }

    // componentDidMount() {
    //     console.log("Before persisting books");
    //     AsyncStorage.setItem('products',
    //         JSON.stringify(productList)).then(() => {
    //         console.log("Yay! content persisted successfully!");
    //     }).catch((error) => {
    //         console.log("Unable to persist the content" + error);
    //     });
    //     AsyncStorage.setItem('deliveries',
    //         JSON.stringify(deliveryList)).then(() => {
    //         console.log("Yay! content persisted successfully!");
    //     }).catch((error) => {
    //         console.log("Unable to persist the content" + error);
    //     });
    //     AsyncStorage.setItem('offers',
    //         JSON.stringify(offersList)).then(() => {
    //         console.log("Yay! content persisted successfully!");
    //     }).catch((error) => {
    //         console.log("Unable to persist the content" + error);
    //     });
    //     this.loadData();
    // }

    async loadData() {

        console.log("Before retrieving content");
        await AsyncStorage.getItem('offers').then((value) => {
            // console.log("Retrieved from storage:" + value);
            this.state.offers = JSON.parse(value);
            console.log(this.state.offers.length);
            AsyncStorage.getItem('products').then((value) => {
                // console.log("Retrieved from storage:" + value);
                this.products = JSON.parse(value);
                console.log(this.products.length);

                this.setState({
                    dataSourceProducts: this.ds.cloneWithRows(this.products)
                });

            }).catch((error) => {
                console.log("Unable to retrieve deliveries" + error);
            });
            AsyncStorage.getItem('deliveries').then((value) => {

                // console.log("Retrieved from storage:" + value);
                this.deliveries = JSON.parse(value);
                console.log(this.deliveries.length);

                this.setState({
                    dataSourceDeliveries: this.ds.cloneWithRows(this.deliveries)
                });
            }).catch((error) => {
                    console.log("Unable to retrieve deliveries" + error);
            });
        }
        ).catch((error) => {
            console.log("Unable to retrieve offers" + error);
        });

    }

    getOffers(productId) {
        let offersOfProducts = [];
        console.log("app all" + this.state.offers.length);
        for(let i=0; i < this.state.offers.length; ++i) {
            if(this.state.offers[i].productId === productId) {
                offersOfProducts = offersOfProducts.concat(this.state.offers[i]);

            }
        }
        console.log("app " + offersOfProducts.length);
        return offersOfProducts;
    }

    getDeliveries(productId) {

        let deliveriesOfProducts = [];

        for(let i=0; i < this.state.offers.length; ++i) {
            if(this.state.offers[i].productId === productId) {

                for(let j=0; j < this.deliveries.length; ++j)
                {
                if(this.deliveries[j].id === this.state.offers[i].deliveryId)
                 {
                     deliveriesOfProducts = deliveriesOfProducts.concat(this.deliveries[j]);
                 }
                }

            }
        }
        return deliveriesOfProducts;
    }


    render() {
        const { navigate } = this.props.navigation;
        this.state.dataSource = this.ds.cloneWithRows(this.products);
        return (
            <View>
                <ScrollView>
                    <ListView
                        dataSource={this.state.dataSource}
                        renderRow={(item) =>
                            <View>
                                <Text
                                    onPress={() =>
                                        navigate('Edit',{
                                            data: {
                                                obj: item.value,
                                                deliveries:this.getDeliveries(item.id),
                                                offers: this.getOffers(item.id),
                                                name: item.name,
                                                productId: item.id,
                                                description: item.description,
                                                allOffers: this.state.offers,
                                                allProducts: this.products
                                            }
                                        })}
                                >{item.name}</Text>
                            </View>}
                    />
                </ScrollView>
                <Button
                    onPress={() => this.popupDialog.show()}
                    title="Add product"
                />
                <PopupDialog
                    ref={(popupDialog) => {
                        this.popupDialog = popupDialog;
                    }}>
                    <View>
                        <Text>Name:</Text>
                        <TextInput
                            onChangeText={(text) => this.setState({name: text})}
                            value={this.state.name}
                        />
                        <Text>Description:</Text>
                        <TextInput
                            onChangeText={(text) => this.setState({description: text})}
                            value={this.state.description}
                        />
                        <Button
                            onPress={() => {
                                this.saveProduct();
                                this.popupDialog.dismiss();
                            }}
                            title="Add offer"
                        />
                    </View>
                </PopupDialog>
            </View>


        );
    }
    async saveProduct() {
        let newProduct = {
            //id:  ,
            //name:  ,
            //description: ,
        };

        console.log("saving " + newProduct);
        this.state.products.push(newProduct);
        this.setState({
            dataSource: this.ds.cloneWithRows(this.state.products)
        }, ()=> this.persistProducts());
    }

    async persistProducts() {
        await AsyncStorage.setItem('products', JSON.stringify(this.state.products)).then(() => {
            console.log("Reviews persisted successfully! :)");
        }).catch((error) => {
            console.log("Unable to persist the content :(" + error);
        });
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
        this.ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            name: "",
            description: "",
            deliveryId: -1,
            date: "",
            price: "",
            productId: -1,
            deliveries: [],
            offers: [],
            allOffers: [],
            dataSource: [],
        };
    }

    render() {

        const {navigate} = this.props.navigation;
        const {params} = this.props.navigation.state;

        this.state.name = params.data.name;
        this.state.description = params.data.description;
        this.state.offers = params.data.offers;
        this.state.deliveries = params.data.deliveries;
        this.state.allOffers = params.data.allOffers;
        this.state.productId = params.data.productId;
        this.state.dataSource = this.ds.cloneWithRows(this.state.deliveries);

        return (
            <View style={{backgroundColor: '#EFEFF4', flex: 1}}>
                <Button
                    onPress={() => this.popupDialog.show()}
                    title="Add offer"
                />

                <Button
                    onPress={() => {
                        if(this.state.products[this.state.productId].name !== item.name
                            || this.state.products[this.state.productId].description !== item.description
                        ){

                            this.state.products[this.state.productId].name = item.name;
                            this.state.products[this.state.productId].description = item.description;

                            const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
                            this.setState({dataSource: ds.cloneWithRows(this.state.products)});

                            objIndex = this.state.products.findIndex((obj => obj.id === item.id));

                            if (objIndex >= 0) {
                                this.state.products[this.state.productId].name = item.name;
                                this.state.products[this.state.productId].description = item.description;
                                this.persistReviews();
                            }
                        }
                    }}
                    title="Update product"
                />

                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({name: text})}
                    value={this.state.name}

                />
                <TextInput
                    style={{marginTop: "5%", width: "90%", borderWidth: 1, backgroundColor: 'white'}}
                    onChangeText={(text) => this.setState({description: text})}
                    value={this.state.description}

                />

                <ListView
                    dataSource={this.state.dataSource}
                    renderRow={(item, sectionID, rowID) =>
                        <View style={{flex: 1, flexDirection: 'row'}}>
                            <Text
                                onPress={() => navigate('Offer Details',
                                    {
                                        deliveryId: params.data.deliveryId, offer: item,
                                        allProducts: params.data.allProducts, allOffers: this.state.allOffers,
                                        onSelect: this.onSelect
                                    })}
                            >{item.name}</Text>
                            <Button
                                onPress={() => this.deleteOffer(rowID)}
                                title="Delete"
                                style={{flex: 1}}
                            />
                        </View>}
                />
                <PopupDialog
                    ref={(popupDialog2) => {
                        this.popupDialog = popupDialog2;
                    }}>
                    <View>
                        <Picker
                            selectedValue={this.state.deliveryId}
                            onValueChange={(itemValue, itemIndex) => this.setState({deliveryId: itemValue})}>
                            <Picker.Item label="Pizza Hut" value="1"/>
                            <Picker.Item label="Pizza Grande" value="2"/>
                            <Picker.Item label="Pizza Venezia" value="3"/>
                        </Picker>
                        <Text>Price:</Text>
                        <TextInput
                            onChangeText={(text) => this.setState({price: text})}
                            value={this.state.price}
                        />
                        <Button
                            onPress={() => {
                                this.saveOffer(this.state.productId);
                                this.popupDialog.dismiss();
                            }}
                            title="Add offer"
                        />
                    </View>
                </PopupDialog>

            </View>
        );
    }

    async saveOffer(product_id2) {
        let newOffer = {
            id: this.getValidId(),
            product_id: product_id2,
            delivery_id: this.state.deliveryId,
            price: this.state.price,
            date: new Date()
        };

        console.log("saving " + newOffer);
        this.state.offers.push(newOffer);
        this.state.allOffers.push(newOffer);
        this.setState({
            dataSource: this.ds.cloneWithRows(this.state.offers)
        }, ()=> this.persistOffers());
    }

    deleteOffer(id) {
        console.log("delete pressed " + id);
        let deletedOffer = this.state.offers.splice(id, 1);
        this.setState({
            dataSource: this.ds.cloneWithRows(this.state.offers)
        });
        let pos = this.state.allOffers.findIndex((obj => obj.id === deletedOffer[0].id)); // position in big list of review to be deleted
        console.log(deletedOffer);
        this.state.allOffers.splice(pos, 1); // 1 = delete count
        this.persistOffers();
    }

    async persistOffers() {
        await AsyncStorage.setItem('offers', JSON.stringify(this.state.allOffers)).then(() => {
            console.log("Reviews persisted successfully! :)");
        }).catch((error) => {
            console.log("Unable to persist the content :(" + error);
        });

    }

    getValidId() {
        let maxId = -1;
        for(let i = 0; i < this.state.allOffers.length; ++i) {
            if(this.state.allOffers[i].id > maxId) {
                maxId = this.state.allOffers[i].id;
            }
        }
        return maxId + 1;
    }



}

const NavigApp = StackNavigator({
    Home: { screen: MainScreen },
    SendEmail: { screen: SendMailScreen},
    Edit: {screen: EditScreen}
});

export default class App extends Component<{}> {
    render() {
        return <NavigApp />;
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    bookTitle: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
        color: 'blue'
    },
});
