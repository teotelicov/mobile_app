import React, {Component} from 'react';
import {
    Button,
    Platform, ScrollView,
    StyleSheet,
    Text,
    View,
    Alert, TouchableHighlight, FlatList, TextInput, Linking
} from 'react-native';
import DatePicker from 'react-native-datepicker'


export default class ProductDetailComponent extends Component<{}> {
    constructor(props) {
        super(props);

        this.handleUpdate = this.handleUpdate.bind(this);
        this.sendEmail = this.sendEmail.bind(this);
        this.deleteProduct = this.deleteProduct.bind(this);
        this.alertDialogShow=this.alertDialogShow.bind(this);

        this.state = {
            firebaseKey:this.props.product.firebaseKey,
            id:this.props.product.id,
            name: this.props.product.name,
            description: this.props.product.description,
            price: this.props.product.price,
            date: this.props.product.date + "",
            rating: this.props.product.rating + ""
        };

        console.log(this.state);
    }

    handleUpdate() {
        this.props.onUpdate({
            firebaseKey:this.props.product.firebaseKey,
            id: this.props.product.id,
            name: this.state.name,
            description: this.state.description,
            price: this.state.price,
            date: this.state.date,
            rating: this.state.rating
        })
    }

    sendEmail() {
        subject = "Product " + this.state.name + " details";
        body = "Name: " + this.state.name + "\n" +
            "Description: " + this.state.description + "\n" +
            "Price: " + this.state.price + "\n" +
            "Date when it was added: " + this.state.date + "\n" +
            "Rating: " + this.state.rating;
        Linking.openURL('mailto:teodora.1996@yahoo.com?subject=' + subject + '&body=' + body);
    }

    deleteProduct() {
        this.props.onDelete(this.props.product.id,this.props.product.firebaseKey);
    }

    alertDialogShow(){
        Alert.alert(
            'Confirm your choice',
            'Are you sure you want to remove this product?',
            [
                {text: 'YES', onPress: () => this.deleteProduct()},
                {text: 'NO', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
            ],
            { cancelable: false }
        )
    }

    render() {
        return (
            <View style={styles.mainContainer}>

                <View>
                    <Text>Name: </Text>
                    <TextInput
                        onChangeText={(text) => this.setState({name: text})}
                        value={this.state.name}
                    />
                </View>
                <View>
                    <Text>Description: </Text>
                    <TextInput
                        onChangeText={(text) => this.setState({description: text})}
                        value={this.state.description}
                    />
                </View>
                <View>
                    <Text>Price: </Text>
                    <TextInput
                        onChangeText={(text) => this.setState({price: text})}
                        keyboardType={"numeric"}
                        maxLength={2}
                        value={this.state.price}
                    />
                </View>
                <View>
                    <Text>Date when it was added: </Text>
                    <DatePicker
                        style={{width: 200}}
                        date={this.state.date}
                        mode="date"
                        placeholder="select date"
                        format="YYYY-MM-DD"
                        confirmBtnText="Confirm"
                        cancelBtnText="Cancel"
                        customStyles={{
                            dateIcon: {
                                position: 'absolute',
                                left: 0,
                                top: 4,
                                marginLeft: 0
                            },
                            dateInput: {
                                marginLeft: 36
                            }
                        }}
                        onDateChange={(date) => {
                            this.setState({date: date})
                        }}
                    />
                </View>

                <View>
                    <Text>Rating: </Text>
                    <TextInput
                        onChangeText={(text) => this.setState({rating: text})}
                        keyboardType={"numeric"}
                        maxLength={2}
                        value={this.state.rating}
                    />
                </View>


                <View style={styles.buttonContainer}>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"Back"}
                            color="#841584"
                            onPress={() => this.props.onComeBack()}
                        />
                    </View>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"Update"}
                            color="#841584"
                            onPress={() => this.handleUpdate()}
                        />
                    </View>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"Send Email"}
                            color="#841584"
                            onPress={() => this.sendEmail()}
                        />
                    </View>

                </View>
                <View style={styles.buttonContainer}>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"Delete"}
                            color="#841584"
                            onPress={() => this.alertDialogShow()}
                        />

                    </View>
                    <View style={{width: 130, padding: 5}}>
                        <Button
                            style={styles.buttonStyle}
                            title={"View chart"}
                            color="#841584"
                            onPress={()=>this.props.loadChart()}
                        />

                    </View>
                </View>
            </View>
        );
    }

}
const styles = StyleSheet.create({
    mainContainer: {
        flex: 1,
    },
    buttonContainer: {
        flex: 2,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    buttonStyle: {
        flex: 1,
    },
});
