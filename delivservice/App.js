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

import ProductDetailComponent from "./ProductDetailComponent";
import AddProductComponent from "./AddProductComponent";
import ProductDetailManagerComponent from "./ProductDetailMangerComponent";
import LoginComponent from "./LoginComponent";
import {firebaseApp} from './components/FirebaseConfig';

export default class App extends Component<{}> {
    constructor(props) {
        super(props);

        this.getProductsForFlatList = this.getProductsForFlatList.bind(this);
        this.getProductListElements = this.getProductListElements.bind(this);
        this.setDetailView = this.setDetailView.bind(this);
        this.getProductDetailComponent = this.getProductDetailComponent.bind(this);
        this.setProductListView = this.setProductListView.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.setAddProductView = this.setAddProductView.bind(this);
        this.addNewProduct = this.addNewProduct.bind(this);
        this.deleteProduct = this.deleteProduct.bind(this);
        this.alertDialogShow = this.alertDialogShow.bind(this);
        this.getUsersHarcodated = this.getUsersHarcodated.bind(this);
        this.getWishListHarcodated = this.getWishListHarcodated.bind(this);

        var users = this.getUsersHarcodated();
        var whislist = this.getWishListHarcodated();


        AsyncStorage.setItem('users', JSON.stringify(users));
        AsyncStorage.setItem('whislist', JSON.stringify(whislist));


        viewElement = this.getProductListElements([]);
        this.state = {products: [], viewElement: viewElement, whishlist: whislist}

        const secondThis = this;
        AsyncStorage.getItem('products').then(v => {
            if (v == undefined) {
                AsyncStorage.setItem('products', JSON.stringify([]));
                AsyncStorage.setItem('counter', '0');
            } else {
                viewElement = secondThis.getProductListElements(JSON.parse(v));
                secondThis.setState({products: JSON.parse(v), viewElement: viewElement});
            }
        });
    }


    getProductsForFlatList(products) {
        products.map(x => x.key = x.id);
        return products;
    }

    componentDidMount() {
        firebaseApp.database().ref().child('products').on('value', (childSnapshot) => {
            var updatedProductList = [];

            childSnapshot.forEach(element => {
                updatedProductList.push(element.val());
            })

            console.log(updatedProductList);
            newViewElement = this.getProductListElements(updatedProductList);
            this.setState({products: updatedProductList, viewElement: newViewElement});
        });
    }

    componentDidUnMount() {
        firebaseApp.database().ref().child('products').off('value');
    }

    alertDialogShow() {
        Alert.alert(
            'Confirm your choice',
            'Are you sure you want to remove all products?',
            [
                {text: 'YES', onPress: () => this.deleteAllProducts()},
                {text: 'NO', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
            ],
            {cancelable: false}
        )
    }

    getUsersHarcodated() {
        return [
            {id: 1, username: "user1", email: "user1@yahoo.com"},
            {id: 2, username: "user2", email: "user2@yahoo.com"},
            {id: 3, username: "user3", email: "user3@yahoo.com"},
            {id: 4, username: "user4", email: "user4@yahoo.com"},
            {id: 5, username: "user5", email: "user5@yahoo.com"},
            {id: 6, username: "user6", email: "user6@yahoo.com"},
            {id: 7, username: "user7", email: "user7@yahoo.com"},
        ];
    }

    getWishListHarcodated() {
        return [
            {usernameId: 1, productId: 1, date: "2017-12-01"},
            {usernameId: 2, productId: 1, date: "2017-12-01"},
            {usernameId: 3, productId: 1, date: "2017-12-02"},
            {usernameId: 4, productId: 1, date: "2017-12-03"},
            {usernameId: 5, productId: 1, date: "2017-12-04"},
            {usernameId: 6, productId: 1, date: "2017-12-04"},
            {usernameId: 7, productId: 1, date: "2017-12-04"},
            {usernameId: 1, productId: 2, date: "2017-12-01"},
            {usernameId: 2, productId: 2, date: "2017-12-01"},
            {usernameId: 3, productId: 2, date: "2017-12-01"},
            {usernameId: 4, productId: 2, date: "2017-12-02"},
            {usernameId: 5, productId: 2, date: "2017-12-03"},
            {usernameId: 6, productId: 2, date: "2017-12-06"},
            {usernameId: 7, productId: 2, date: "2017-12-06"},
            {usernameId: 1, productId: 3, date: "2017-12-03"},
            {usernameId: 2, productId: 3, date: "2017-12-03"},
            {usernameId: 3, productId: 3, date: "2017-12-03"},
            {usernameId: 4, productId: 3, date: "2017-12-04"},
            {usernameId: 5, productId: 3, date: "2017-12-05"},
            {usernameId: 6, productId: 3, date: "2017-12-05"},
            {usernameId: 7, productId: 3, date: "2017-12-06"}
        ];
    }


    getProductListElements(products) {
        myProducts = this.getProductsForFlatList(products);
        return (
            <View style={styles.mainView}>
              <FlatList
                  style={styles.listView}
                  data={myProducts}
                  renderItem={({item}) =>
                      <TouchableHighlight onPress={() => {
                          this.setDetailView(item.id)
                      }} underlayColor="azure">

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
                      title={"Add Product"}
                      color="#841584"
                      onPress={() => this.setAddProductView()}
                  />
                </View>

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
        );

    }

    signOut() {
        firebaseApp.auth().signOut();
        newElement = <LoginComponent/>;
        this.setState({viewElement: newElement});
    }

    addNewProduct(product) {
        var products = this.state.products.slice();

        let key = firebaseApp.database().ref().child('products').push().key;

        AsyncStorage.getItem('counter').then(v => {
            var id = parseInt(v) + 1;
            firebaseApp.database().ref('products').child(key).update({
                firebaseKey: key,
                id:id,
                name: product.name,
                description: product.description,
                price: product.price,
                date: product.date,
                rating: product.rating
            });
        });

        AsyncStorage.getItem('counter').then(v => {
            var newCounter = parseInt(v) + 1;
            product.id = newCounter;
            product.firebaseKey = key;
            products.push(product);
            AsyncStorage.setItem('counter', "" + newCounter);
            AsyncStorage.setItem('products', JSON.stringify(products));
            this.setState({products: products, viewElement: this.getProductListElements(products)});
        });

    }

    deleteProduct(productId, firebaseKey) {
        try {
            firebaseApp.database().ref().child('products').child(firebaseKey).remove();
        } catch (err) {
        }

        let filteredProducts = this.state.products;
        filteredProducts = filteredProducts.filter(element => element.id != productId);
        this.setState({products: filteredProducts, viewElement: this.getProductListElements(filteredProducts)});
        AsyncStorage.setItem('products', JSON.stringify(filteredProducts));

    }

    setAddProductView() {
        newElement = <AddProductComponent
            addProduct={this.addNewProduct}
            onComeBack={() => {
                this.setProductListView()
            }}
        />;
        this.setState({viewElement: newElement});
    }


    setDetailView(productId) {
        product = this.state.products.find(b => b.id === productId);
        newElement = this.getProductDetailComponent(product);
        this.setState({viewElement: newElement});
    }


    getProductDetailComponent(product) {
        var list = this.state.whishlist;
        list = list.filter(element => element.productId == product.id);
        return <ProductDetailManagerComponent
            wishlist={list}
            product={product}
            onUpdate={this.handleUpdate}
            onDelete={this.deleteProduct}
            onComeBack={() => {
                this.setProductListView()
            }}
        />;
    }

    setProductListView() {
        this.setState({viewElement: this.getProductListElements(this.state.products)});
    }

    handleUpdate(product) {
        try {
            firebaseApp.database().ref("products").child(product.firebaseKey).update(product);
        } catch (err) {
        }
        newProducts = this.state.products;
        newProducts[newProducts.findIndex(el => el.id === product.id)] = product;
        AsyncStorage.setItem('products', JSON.stringify(newProducts));
        this.setState({products: newProducts, viewElement: this.getProductListElements(this.state.products)});
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
