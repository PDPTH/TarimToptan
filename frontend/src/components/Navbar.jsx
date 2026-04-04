import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { FiShoppingCart, FiUser, FiLogOut, FiMenu, FiX, FiPackage, FiUsers } from 'react-icons/fi'
import { GiWheat } from 'react-icons/gi'

export default function Navbar() {
    const { isAuthenticated, user, logout } = useAuth()
    const location = useLocation()
    const [menuOpen, setMenuOpen] = useState(false)

    const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link'

    return (
        <nav className="navbar">
            <div className="navbar-inner">
                <Link to="/" className="navbar-logo">
                    <GiWheat className="logo-icon" />
                    TarımToptan
                </Link>

                <button className="navbar-hamburger" onClick={() => setMenuOpen(!menuOpen)} aria-label="Menü">
                    {menuOpen ? <FiX /> : <FiMenu />}
                </button>

                <div className={`navbar-links ${menuOpen ? 'open' : ''}`}>
                    <Link to="/products" className={isActive('/products')} onClick={() => setMenuOpen(false)}>
                        <FiPackage /> Ürünler
                    </Link>
                    <Link to="/producers" className={isActive('/producers')} onClick={() => setMenuOpen(false)}>
                        <FiUsers /> Üreticiler
                    </Link>

                    {isAuthenticated ? (
                        <>
                            <Link to="/cart" className={isActive('/cart')} onClick={() => setMenuOpen(false)}>
                                <FiShoppingCart /> Sepet
                            </Link>
                            <Link to="/orders" className={isActive('/orders')} onClick={() => setMenuOpen(false)}>
                                <FiPackage /> Siparişlerim
                            </Link>
                            <Link to="/profile" className={isActive('/profile')} onClick={() => setMenuOpen(false)}>
                                <FiUser /> {user?.firstName || 'Profil'}
                            </Link>
                            <button
                                className="nav-link"
                                onClick={() => { logout(); setMenuOpen(false) }}
                                style={{ border: 'none', background: 'none', cursor: 'pointer' }}
                            >
                                <FiLogOut /> Çıkış
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className={isActive('/login')} onClick={() => setMenuOpen(false)}>
                                Giriş Yap
                            </Link>
                            <Link to="/register" className="btn btn-primary btn-sm" onClick={() => setMenuOpen(false)}>
                                Kayıt Ol
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    )
}
