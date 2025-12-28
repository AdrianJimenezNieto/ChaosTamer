import React from 'react';
import ReactDom from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import { BrowserRouter } from 'react-router-dom';

ReactDom.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    {/* Wrap the app */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
)