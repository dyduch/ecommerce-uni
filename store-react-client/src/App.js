import './App.css';
import {ProductPreview} from "./ProductPreview";

function App() {
  return (
    <div className="App">
      <header>
      </header>
      <main>
        <ProductPreview image="/assets/images/adi-bus-vulc-2.jpg" name="Adidas Busenitz Vulc II" color="white/black/gum" price="300" currency="PLN"></ProductPreview>
      </main>
    </div>
  );
}

export default App;
