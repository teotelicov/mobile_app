import React, {Component} from 'react';

import {
    AsyncStorage,
    Button,
    Platform, ScrollView,
    StyleSheet,
    Text,
    View,
    Alert, TouchableHighlight, FlatList
} from 'react-native';
import {firebaseApp} from './components/FirebaseConfig';
import LoginComponent from "./LoginComponent";

export default class UserProductList extends Component<{}> {
    constructor(props) {
        super(props);
        this.getProductsForFlatList = this.getProductsForFlatList.bind(this);
        this.signOut = this.signOut.bind(this);
        this.getProductListElement = this.getProductListElement.bind(this);


        viewElement = this.getProductListElement([]);
        this.state = {products: [], viewElement: viewElement}

    }

    componentDidMount() {
        firebaseApp.database().ref().child('products').on('value', (childSnapshot) => {
            var updatedProductList = [];

            childSnapshot.forEach(element => {
                updatedProductList.push(element.val());
            })

            console.log(updatedProductList);
            newViewElement = this.getProductListElement(updatedProductList);
            this.setState({products: updatedProductList, viewElement: newViewElement});
        });
    }

    componentDidUnMount() {
        firebaseApp.database().ref().child('products').off('value');
    }

    getProductsForFlatList(products) {
        products.map(x => x.key = x.id);
        return products;
    }

    signOut() {
        firebaseApp.auth().signOut();
        newElement = <LoginComponent/>;
        this.setState({viewElement: newElement});
    }

    getProductListElement(products) {
        myProducts = this.getProductsForFlatList(products);
        return (
            <View style={styles.mainView}>
                <FlatList
                    style={styles.listView}
                    data={myProducts}
                    renderItem={({item}) =>
                        <TouchableHighlight  underlayColor="azure">

                            <View style={styles.listItemView}>
                                <Text style={styles.bigBlack}>
                                    {" Name: " + item.name + "\n Description: " + item.description + "\n Rating: " + item.rating}
                                </Text>
                            </View>

                        </TouchableHighlight>
                    }
                />

                <View style={styles.buttonContainer}>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"Sign out"}
                            color="#841584"
                            onPress={() => this.signOut()}
                        />
                    </View>

                </View>
            </View>
        )
    }

    render() {
        return this.state.products === null ? null : (
            this.state.viewElement
        );
    }
}

const styles = StyleSheet.create({
    mainView: {
        flex: 1
    },
    listView: {
        padding: 10,
        flex: 0.8,
        backgroundColor: 'white',
    },
    listItemView: {
        padding: 5
    },
    bigBlack: {
        fontSize: 15,
        fontWeight: 'bold',
    },
    buttonContainer: {
        flex: 0.2,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    buttonStyle: {
        flex: 1,
    },
});