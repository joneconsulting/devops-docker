import axios from 'axios'; 

const BOARD_API_BASE_URL = "http://localhost:8088/catalogs"; 

class BoardService {
    getBoards() {
        return axios.get(BOARD_API_BASE_URL);
    }
}

export default new BoardService();