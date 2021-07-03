import React, {Component} from "react";

export class Checkout extends Component {
    constructor(props) {
        super(props);
        this.state = {
            fullName: '',
            address: '',
            payment: 'BLIK'
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        this.setState({
            [name]: value    });
    }

    handleSubmit(event) {
        alert('Order placed');
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label>
                    Name:
                    <input
                        name="fullName"            type="string"
                        value={this.state.fullName}
                        onChange={this.handleInputChange} />
                </label>
                <br />
                <label>
                    Address:
                    <input
                        name="address"            type="string"
                        value={this.state.address}
                        onChange={this.handleInputChange} />
                </label>
                <br />
                <label>
                    Payment method:
                    <select value={this.state.value} onChange={this.handleInputChange}>
                        <option value="BLIK">BLIK</option>
                        <option value="cash">Cash</option>
                    </select>
                </label>
                <input type="submit" value="Submit" />
            </form>
        );
    }
}