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
import ProductDetailComponent from "./ProductDetailComponent";
import ChartComponent from "./ChartComponent";


export default class ProductDetailManagerComponent extends Component<{}> {

    constructor(props) {
        super(props);
        this.loadChartComponent=this.loadChartComponent.bind(this);
        this.loadDetailComponent=this.loadDetailComponent.bind(this);

        let element=(<ProductDetailComponent
            wishlist={this.props.wishlist}
            product={this.props.product}
            onUpdate={this.props.onUpdate}
            onDelete={this.props.onDelete}
            onComeBack={this.props.onComeBack}
            loadChart={this.loadChartComponent}
        />);
        this.state={element:element}

    }


    loadChartComponent(){
        let element=<ChartComponent
            onComeBack={this.loadDetailComponent}
            wishlist={this.props.wishlist}
        />
        this.setState({element:element})
    }
    loadDetailComponent(){
        let element=(<ProductDetailComponent
            wishlist={this.props.whishlist}
            product={this.props.product}
            onUpdate={this.props.onUpdate}
            onDelete={this.props.onDelete}
            onComeBack={this.props.onComeBack}
            loadChart={this.loadChartComponent}
        />);
        this.setState({element:element})
    }
    render(){
        return this.state.element;
    }
}