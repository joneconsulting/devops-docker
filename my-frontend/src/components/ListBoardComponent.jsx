import React, { Component } from 'react';
import BoardService from '../service/BoardService';

class ListBoardComponent extends Component {
    constructor(props) {
        super(props)
    // # 1. 
        this.state = { 
            boards: []
        }
		
    }
    // # 2. 
    componentDidMount() {
        BoardService.getBoards().then((res) => {
            this.setState({ boards: res.data});
        });
    }

    // # 3.
    render() {
        return (
            <div>
                <h2 className="text-center">Catalog List</h2>
                <div className ="row">
                    <table className="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>상품 ID</th>
                                <th>상품명 </th>
                                <th>수량 </th>
                                <th>단가 </th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                this.state.boards.map(
                                    board => 
                                    <tr key = {board.productId}>
                                        <td> {board.productId} </td>
                                        <td> {board.productName} </td>
                                        <td> {board.stock} </td>
                                        <td> {board.unitPrice} </td>
                                    </tr>
                                )
                            }
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}

export default ListBoardComponent;