import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import { MapPin, Star, Wifi, Coffee, Waves, CheckCircle, ArrowLeft, Loader2, Calendar } from 'lucide-react';
import { getPropertyById } from '../../services/property.services';
import { createBooking, confirmBookingPayment } from '../../services/booking.services';

export default function PropertyDetail() {
    const { id } = useParams(); // Obtenemos el ID de la URL
    const [property, setProperty] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isPaid, setIsPaid] = useState(false);

    // Booking states
    const [checkIn, setCheckIn] = useState('');
    const [checkOut, setCheckOut] = useState('');
    const [bookingError, setBookingError] = useState('');
    const [isBookingCreated, setIsBookingCreated] = useState(false);
    const [bookingId, setBookingId] = useState(null);
    const [isCreating, setIsCreating] = useState(false);

    useEffect(() => {
        fetchPropertyData();
    }, [id]);

    const fetchPropertyData = async () => {
        setLoading(true);
        try {
            const data = await getPropertyById(id);
            setProperty(data);
        } catch (error) {
            console.error("Error al obtener la propiedad", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center py-32">
                <Loader2 className="animate-spin text-secondary" size={40} />
                <span className="ml-3 text-gray-500 text-lg font-medium">Cargando detalles...</span>
            </div>
        );
    }

    if (!property) {
        return (
            <div className="text-center py-32 text-gray-500 text-lg border m-20 rounded-2xl bg-white shadow-xl">
                <h3 className="text-2xl font-bold text-gray-800 mb-2">Propiedad no encontrada</h3>
                <p className="text-gray-500 mb-6">No se encontró la propiedad solicitada o ha sido eliminada.</p>
                <Link to="/" className="inline-flex items-center text-white bg-secondary px-6 py-2 rounded-lg hover:opacity-80 font-semibold transition-colors">
                    <ArrowLeft size={20} className="mr-2" /> Volver al catálogo
                </Link>
            </div>
        );
    }

    // Cálculos dinámicos de noches
    let calculatedNights = 1;
    if (checkIn && checkOut) {
        const d1 = new Date(checkIn);
        const d2 = new Date(checkOut);
        const timeDiff = d2.getTime() - d1.getTime();
        const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
        if (daysDiff > 0) calculatedNights = daysDiff;
    }

    const subtotal = property.price * calculatedNights;
    const taxes = subtotal * 0.18;
    const total = subtotal + taxes;

    // Crear la reserva en estado PENDIENTE en el backend
    const handleCreateBooking = async () => {
        setBookingError('');
        const userStr = localStorage.getItem('user');
        if (!userStr) {
            setBookingError("Debes iniciar sesión para reservar.");
            return;
        }

        if (!checkIn || !checkOut) {
            setBookingError("Selecciona las fechas de Check-In y Check-Out.");
            return;
        }

        if (new Date(checkIn) >= new Date(checkOut)) {
            setBookingError("La fecha de salida debe ser posterior a la de entrada.");
            return;
        }

        const user = JSON.parse(userStr);
        setIsCreating(true);

        try {
            const reservaData = {
                clienteId: user.email, // Asumiendo usaremos el email como ID del cliente o user.id
                propiedadId: property.id,
                fechaInicio: checkIn,
                fechaFin: checkOut,
                totalPagar: total
            };

            const nuevaReserva = await createBooking(reservaData);
            setBookingId(nuevaReserva.id);
            setIsBookingCreated(true);
        } catch (error) {
            console.error("Error validando disponibilidad:", error);
            setBookingError(error.response?.data?.error || "La habitación no está disponible para estas fechas.");
        } finally {
            setIsCreating(false);
        }
    };

    // Función que se ejecuta cuando PayPal aprueba y captura el pago
    const handlePaymentSuccess = async (details, data) => {
        try {
            // El JS SDK ya capturó los fondos -- solo notificamos al backend para actualizar estado
            await confirmBookingPayment(bookingId, details.id);
            setIsPaid(true);
        } catch (error) {
            console.error("Error confirmando el pago:", error);
            setBookingError("El pago fue recibido por PayPal pero hubo un error de validación en el servidor.");
        }
    };

    return (
        <div className="max-w-7xl mx-auto">

            <Link to="/" className="inline-flex items-center text-secondary hover:opacity-80 font-semibold mb-6 transition-colors">
                <ArrowLeft size={20} className="mr-1" /> Volver al catálogo
            </Link>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

                <div className="lg:col-span-2 space-y-6">
                    <img
                        src={property.image || 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&q=80&w=1200'}
                        alt={property.name}
                        className="w-full h-96 object-cover rounded-2xl shadow-md"
                    />

                    <div className="flex justify-between items-start">
                        <div>
                            <div className="flex gap-2 mb-3">
                                <span className="bg-secondary/10 text-secondary border border-secondary/20 px-3 py-1 rounded-full text-xs font-bold shadow-sm uppercase tracking-wider">{property.type}</span>
                                <span className="bg-gray-100 text-gray-600 border border-gray-200 px-3 py-1 rounded-full text-xs font-bold shadow-sm uppercase tracking-wider">{property.roomType}</span>
                            </div>
                            <h1 className="text-3xl font-bold text-gray-800">{property.name}</h1>
                            <div className="flex items-center text-gray-500 mt-2">
                                <MapPin size={18} className="mr-1" /> {property.location}
                            </div>
                        </div>
                        <div className="flex items-center bg-gray-50 px-3 py-1 rounded-lg border border-gray-100">
                            <Star size={20} className="text-secondary fill-current" />
                            <span className="ml-1 font-bold text-gray-800 text-lg">4.8</span>
                            <span className="text-gray-400 text-sm ml-1">(0)</span>
                        </div>
                    </div>

                    <hr />

                    <div>
                        <h3 className="text-xl font-bold text-gray-800 mb-3">Descripción</h3>
                        <p className="text-gray-600 leading-relaxed min-h-[4rem]">{property.description}</p>
                    </div>

                    <div>
                        <h3 className="text-xl font-bold text-gray-800 mb-3">Amenidades Populares</h3>
                        <div className="flex flex-wrap gap-4">
                            {property.amenities && property.amenities.map((amenity, index) => (
                                <div key={index} className="flex items-center text-gray-600 bg-gray-100 px-4 py-2 rounded-lg">
                                    <CheckCircle size={18} className="mr-2 text-secondary" /> {amenity}
                                </div>
                            ))}
                            {(!property.amenities || property.amenities.length === 0) && (
                                <p className="text-gray-500">No hay amenidades registradas para esta propiedad.</p>
                            )}
                        </div>
                    </div>
                </div>

                <div>
                    <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100 sticky top-24">

                        {isPaid ? (
                            <div className="text-center py-8">
                                <CheckCircle size={60} className="text-green-500 mx-auto mb-4" />
                                <h3 className="text-2xl font-bold text-gray-800 mb-2">¡Reserva Confirmada!</h3>
                                <p className="text-gray-500 mb-6">Hemos enviado la factura a tu correo electrónico.</p>
                                <div className="bg-gray-50 p-4 rounded-lg text-left mb-6">
                                    <p className="text-sm text-gray-500">Código de Reserva: <strong className="text-gray-800">#HTL-9842</strong></p>
                                    <p className="text-sm text-gray-500">Monto Pagado: <strong className="text-gray-800">${total.toFixed(2)}</strong></p>
                                </div>
                                <Link to="/my-bookings" className="block w-full bg-secondary text-white font-bold py-3 rounded-lg hover:opacity-90 transition-all text-center">
                                    Ver mis reservas
                                </Link>
                            </div>
                        ) : (
                            <>
                                <div className="mb-4">
                                    <span className="text-3xl font-bold text-gray-800">${property.price}</span>
                                    <span className="text-gray-500"> / noche</span>
                                </div>

                                <div className="space-y-4 mb-6">
                                    <div className="flex flex-col gap-3">
                                        <div>
                                            <label className="text-sm font-bold text-gray-700 mb-1 block">Check-In</label>
                                            <div className="relative">
                                                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={16} />
                                                <input
                                                    type="date"
                                                    value={checkIn}
                                                    onChange={(e) => setCheckIn(e.target.value)}
                                                    disabled={isBookingCreated}
                                                    className="w-full pl-9 pr-3 py-2 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-secondary/50 text-sm"
                                                />
                                            </div>
                                        </div>
                                        <div>
                                            <label className="text-sm font-bold text-gray-700 mb-1 block">Check-Out</label>
                                            <div className="relative">
                                                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={16} />
                                                <input
                                                    type="date"
                                                    value={checkOut}
                                                    onChange={(e) => setCheckOut(e.target.value)}
                                                    disabled={isBookingCreated}
                                                    className="w-full pl-9 pr-3 py-2 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-secondary/50 text-sm"
                                                />
                                            </div>
                                        </div>
                                    </div>

                                    {bookingError && (
                                        <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm font-medium border border-red-100">
                                            {bookingError}
                                        </div>
                                    )}
                                </div>

                                {/* Desglose de precio */}
                                <div className="bg-gray-50 p-4 rounded-lg space-y-2 mb-6 text-sm">
                                    <div className="flex justify-between text-gray-600">
                                        <span>${property.price} x {calculatedNights} noches</span>
                                        <span>${subtotal.toFixed(2)}</span>
                                    </div>
                                    <div className="flex justify-between text-gray-600">
                                        <span>Impuestos (18%)</span>
                                        <span>${taxes.toFixed(2)}</span>
                                    </div>
                                    <hr className="my-2 border-gray-200" />
                                    <div className="flex justify-between font-bold text-lg text-gray-800">
                                        <span>Total (USD)</span>
                                        <span>${total.toFixed(2)}</span>
                                    </div>
                                </div>

                                {!isBookingCreated ? (
                                    <button
                                        onClick={handleCreateBooking}
                                        disabled={isCreating}
                                        className="w-full bg-secondary text-white font-bold py-3 rounded-lg hover:opacity-90 transition-all flex justify-center items-center gap-2"
                                    >
                                        {isCreating && <Loader2 size={18} className="animate-spin" />}
                                        Reservar Habitación
                                    </button>
                                ) : (
                                    <div className="relative z-0 animate-in fade-in slide-in-from-bottom-4 duration-500">
                                        <div className="bg-green-50 text-green-700 p-3 rounded-lg text-sm font-medium border border-green-100 mb-4 text-center">
                                            Fechas validadas. Completa el pago.
                                        </div>
                                        <PayPalScriptProvider options={{ "client-id": "test", currency: "USD" }}>
                                            <PayPalButtons
                                                style={{ layout: "vertical", color: "blue", shape: "rect" }}
                                                createOrder={(data, actions) => {
                                                    return actions.order.create({
                                                        purchase_units: [{ amount: { value: total.toFixed(2) } }]
                                                    });
                                                }}
                                                onApprove={(data, actions) => {
                                                    // El cliente captura los fondos (ya que él creó la orden con el JS SDK)
                                                    return actions.order.capture().then((details) => {
                                                        handlePaymentSuccess(details, data);
                                                    });
                                                }}
                                            />
                                        </PayPalScriptProvider>
                                    </div>
                                )}
                                <p className="text-xs text-center text-gray-400 mt-4">Transacción segura y encriptada</p>
                            </>
                        )}
                    </div>
                </div>

            </div>
        </div>
    );
}