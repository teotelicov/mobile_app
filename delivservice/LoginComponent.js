
import React, {Component} from 'react';
import {Input} from "./components/Input";
import {Button} from "./components/Button";
import App from "./App"
import {
    StyleSheet,
    Text,
    View,
    TextInput,
    ActivityIndicator,
    Alert

} from 'react-native';
import {firebaseApp} from './components/FirebaseConfig';
import UserProductList from "./UserProductList";


export default class LoginComponent extends Component<{}> {

    constructor(props) {
        super(props);

        this.renderCurrentState = this.renderCurrentState.bind(this);
        this.onPressSignIn = this.onPressSignIn.bind(this);
        this.onPressRegister = this.onPressRegister.bind(this);

        this.state = {
            email: '',
            password: '',
            user:null,
            authenticating:false
        }
    }

    componentDidMount() {

        firebaseApp.auth().onAuthStateChanged((user) => {
            this.setState({
                user: user,
                authenticating: true
            });
        });
    }

    componentWillMount() {
        firebaseApp.auth().signOut();
        firebaseApp.auth().onAuthStateChanged((user) => {
            this.setState({
                user: user,
                authenticating: true
            });
        });
    }

    onPressSignIn() {
        firebaseApp.auth().signInWithEmailAndPassword(this.state.email, this.state.password)
            .catch((error) => {
            const {code, message} = error;
            Alert.alert(
                'Log in failed',
                error.message,
                [
                    {text: 'Go back', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
                ],
                {cancelable: true}
            )
        });

    }

    onPressRegister() {
        firebaseApp.auth().createUserWithEmailAndPassword(this.state.email, this.state.password)
            .catch((error) => {
            const {code, message} = error;
            Alert.alert(
                'Register failed',
                error.message,
                [
                    {text: 'Go back', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
                ],
                {cancelable: true}
            )
        });
    }

    renderCurrentState() {
        return (
            <View style={styles.form}>
                <Input
                    placeholder='Enter your email'
                    label='Email'
                    onChangeText={email => this.setState({email: email})}
                    value={this.state.email}
                />
                <Input
                    placeholder='Enter your password'
                    label='Password'
                    secureTextEntry
                    onChangeText={password => this.setState({password: password})}
                    value={this.state.password}
                />

                <Button onPress={() => this.onPressSignIn()}>Log In</Button>
                <Button onPress={() => this.onPressRegister()}>Register</Button>
            </View>
        )

    }


    render() {
        if(firebaseApp.auth().currentUser !=null){
            if(firebaseApp.auth().currentUser.email=="teodora.1996@yahoo.com") {
                return (
                    <App/>
                )
            }else{
                return(
                    <UserProductList/>

                )
            }
        }else{
            return(
                <View style={styles.container}>
                    {this.renderCurrentState()}
                </View>
            )
        }
    }


}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        alignItems: 'center',
        justifyContent: 'center',
        flexDirection: 'row',
        backgroundColor: '#00ced1',
    },
    form: {
        flex: 1
    }
});