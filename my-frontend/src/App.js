import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'; 
import ListBoardComponent from './components/ListBoardComponent';
import HeaderComponent from './components/HeaderComponent';
import FooterComponent from './components/FooterComponent';

function App() {
  return (
    <div> 
      <Router>             
        <HeaderComponent/> 
          <div className="container">
            <Routes>       
              <Route path = "/" exact element = {<ListBoardComponent />}></Route>
              <Route path = "/catalogs" element = {<ListBoardComponent />}></Route>
            </Routes>
          </div>
        <FooterComponent/> 
      </Router>
    </div>
  );
}

export default App;
