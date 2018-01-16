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
import {firebaseApp} from './components/FirebaseConfig';

export default class AddProductComponent extends Component<{}> {
    constructor(props) {
        super(props);

        this.addProduct = this.addProduct.bind(this);

        this.state = {
            name: "",
            description: "",
            price: "",
            date: "",
            rating: "",
        };
    }

    addProduct() {

        this.props.addProduct({
            name: this.state.name,
            description: this.state.description,
            price: this.state.price,
            date: this.state.date,
            rating: this.state.rating
        })
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
                    <Text>Decsription: </Text>
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
                    <Text>Date of aparition: </Text>
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
                        onDateChange={(date) => {this.setState({date: date})}}
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
                            title={"Add Product"}
                            color="#841584"
                            onPress={() => this.addProduct()}
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