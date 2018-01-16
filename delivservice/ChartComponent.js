/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
    Button,
    Platform,
    StyleSheet,
    Text,
    View
} from 'react-native';


import {Bar} from 'react-native-pathjs-charts'

const instructions = Platform.select({
    ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
    android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

export default class ChartComponent extends Component<{}> {
    constructor(props){
        super(props);

        this.getDataChart=this.getDataChart.bind(this);
    }
    // static navigationOptions = ({navigation}) => ({
    //     title: `Bar (Column) - Basic`,
    // });

    getDataChart(list){
        let list1=list;
        let dataChart=[];
        while(list1.length > 0){
            let date=list1[0].date;
            let counter=list.filter(element => element.date==date).length;
            dataChart.push([{"name":date,"v":counter}]);
            list1=list1.filter(element => element.date!=date);
        }

        return dataChart;
    }

    render() {
        let data = this.getDataChart(this.props.wishlist);

        let options = {
            width: 300,
            height: 300,
            margin: {
                top: 20,
                left: 25,
                bottom: 50,
                right: 20
            },
            color: '#2980B9',
            gutter: 20,
            animate: {
                type: 'oneByOne',
                duration: 200,
                fillTransition: 3
            },
            axisX: {
                showAxis: true,
                showLines: true,
                showLabels: true,
                showTicks: true,
                zeroAxis: false,
                orient: 'bottom',
                label: {
                    fontFamily: 'Arial',
                    fontSize: 8,
                    fontWeight: true,
                    fill: '#34495E',
                    rotate: 45
                }
            },
            axisY: {
                showAxis: true,
                showLines: true,
                showLabels: true,
                showTicks: true,
                zeroAxis: false,
                orient: 'left',
                label: {
                    fontFamily: 'Arial',
                    fontSize: 8,
                    fontWeight: true,
                    fill: '#34495E'
                }
            }
        }

        return (
            <View style={styles.container}>
                <Bar data={data} options={options} accessorKey='v'/>
                <View style={{width: 130, padding: 5}}>
                    <Button
                        style={styles.buttonStyle}
                        title={"Back"}
                        color="#841584"
                        onPress={() => this.props.onComeBack()}
                    />
                </View>
            </View>

        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
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
