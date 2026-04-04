import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'

// Pages
import HomePage from './pages/HomePage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import ResetPasswordPage from './pages/ResetPasswordPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import ProductsPage from './pages/ProductsPage.jsx'
import ProductDetailPage from './pages/ProductDetailPage.jsx'
import ProductManagePage from './pages/ProductManagePage.jsx'
import ProducersPage from './pages/ProducersPage.jsx'
import ProducerDetailPage from './pages/ProducerDetailPage.jsx'
import CartPage from './pages/CartPage.jsx'
import CheckoutPage from './pages/CheckoutPage.jsx'
import AddressPage from './pages/AddressPage.jsx'
import OrdersPage from './pages/OrdersPage.jsx'

function App() {
    return (
        <div className="app-wrapper">
            <Navbar />
            <main className="main-content">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/reset-password" element={<ResetPasswordPage />} />
                    <Route path="/products" element={<ProductsPage />} />
                    <Route path="/products/:productId" element={<ProductDetailPage />} />
                    <Route path="/producers" element={<ProducersPage />} />
                    <Route path="/producers/:producerId" element={<ProducerDetailPage />} />

                    {/* Protected Routes */}
                    <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
                    <Route path="/products/manage" element={<ProtectedRoute><ProductManagePage /></ProtectedRoute>} />
                    <Route path="/products/manage/:productId" element={<ProtectedRoute><ProductManagePage /></ProtectedRoute>} />
                    <Route path="/cart" element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
                    <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
                    <Route path="/addresses" element={<ProtectedRoute><AddressPage /></ProtectedRoute>} />
                    <Route path="/orders" element={<ProtectedRoute><OrdersPage /></ProtectedRoute>} />
                </Routes>
            </main>
            <Footer />
        </div>
    )
}

export default App
