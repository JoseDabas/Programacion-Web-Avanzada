import { useState, useEffect, useMemo } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { CalendarCheck, CreditCard, XCircle, TrendingUp, RefreshCw, Loader2 } from 'lucide-react';
import { getAllBookings } from '../../services/booking.services';

const REFRESH_INTERVAL = 30000;

const DIAS_SEMANA = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];

export default function AdminDashboard() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [lastUpdate, setLastUpdate] = useState(null);

    // Función para cargar datos
    const fetchBookings = async (showLoading = false) => {
        try {
            if (showLoading) setLoading(true);
            const data = await getAllBookings();
            setBookings(data);
            setError(null);
            setLastUpdate(new Date());
        } catch (err) {
            console.error('Error cargando reservas:', err);
            setError('No se pudieron cargar los datos del dashboard.');
        } finally {
            setLoading(false);
        }
    };

    // Carga inicial + polling cada 30s
    useEffect(() => {
        fetchBookings(true);
        const interval = setInterval(() => fetchBookings(false), REFRESH_INTERVAL);
        return () => clearInterval(interval);
    }, []);

    // --- Cálculos derivados con useMemo ---
    // Helper: formatea un Date local como "YYYY-MM-DD" (sin depender de UTC)
    const toLocalDateStr = (date) => {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
    };

    const todayStr = useMemo(() => toLocalDateStr(new Date()), []);

    // Contadores para las tarjetas de estadísticas
    const stats = useMemo(() => {
        // "Reservas del Día" = reservas activas hoy (fechaInicio <= hoy <= fechaFin)
        const reservasDelDia = bookings.filter(b =>
            b.fechaInicio <= todayStr && b.fechaFin >= todayStr && b.estado !== 'CANCELADO'
        ).length;
        const pendientes = bookings.filter(b => b.estado === 'PENDIENTE').length;
        const completadas = bookings.filter(b => b.estado === 'COMPLETADO').length;
        const canceladas = bookings.filter(b => b.estado === 'CANCELADO').length;
        return { reservasDelDia, pendientes, completadas, canceladas };
    }, [bookings, todayStr]);

    // Datos para el gráfico de barras — últimos 7 días
    const barData = useMemo(() => {
        const days = [];
        for (let i = 6; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);
            const dateStr = toLocalDateStr(date);
            const dayName = DIAS_SEMANA[date.getDay()];
            // Contar reservas activas en ese día (fechaInicio <= día <= fechaFin)
            const count = bookings.filter(b =>
                b.fechaInicio <= dateStr && b.fechaFin >= dateStr && b.estado !== 'CANCELADO'
            ).length;
            days.push({ name: dayName, reservas: count, fecha: dateStr });
        }
        return days;
    }, [bookings]);

    // Datos para el gráfico de dona — distribución por estado
    const pieData = useMemo(() => {
        return [
            { name: 'Completadas', value: stats.completadas, color: '#10B981' },
            { name: 'Pendientes', value: stats.pendientes, color: '#F59E0B' },
            { name: 'Canceladas', value: stats.canceladas, color: '#EF4444' },
        ];
    }, [stats]);

    // Estado de carga inicial
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto flex flex-col items-center justify-center py-32 text-gray-500">
                <Loader2 size={48} className="animate-spin mb-4 text-blue-500" />
                <p className="text-lg font-medium">Cargando datos del dashboard...</p>
            </div>
        );
    }

    // Estado de error
    if (error && bookings.length === 0) {
        return (
            <div className="max-w-7xl mx-auto flex flex-col items-center justify-center py-32">
                <div className="bg-red-50 border border-red-200 rounded-lg p-8 text-center max-w-md">
                    <XCircle size={48} className="text-red-400 mx-auto mb-4" />
                    <p className="text-red-700 font-medium text-lg mb-2">Error al cargar datos</p>
                    <p className="text-red-500 text-sm mb-4">{error}</p>
                    <button
                        onClick={() => fetchBookings(true)}
                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                    >
                        Reintentar
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto space-y-6">

            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold text-gray-800">Dashboard General</h2>
                <div className="flex items-center gap-3">
                    {lastUpdate && (
                        <span className="text-xs text-gray-400">
                            Última actualización: {lastUpdate.toLocaleTimeString('es-DO')}
                        </span>
                    )}
                    <button
                        onClick={() => fetchBookings(false)}
                        className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-600 transition-colors"
                        title="Actualizar datos"
                    >
                        <RefreshCw size={18} />
                    </button>
                </div>
            </div>

            {/* Banner de error parcial (datos cargados pero refresh falló) */}
            {error && bookings.length > 0 && (
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg px-4 py-3 text-yellow-700 text-sm">
                    ⚠️ No se pudieron actualizar los datos. Mostrando última información disponible.
                </div>
            )}

            {/* Tarjetas de Estadísticas Principales */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard icon={<TrendingUp size={28} className="text-blue-500" />} title="Reservas del Día" value={stats.reservasDelDia} />
                <StatCard icon={<CalendarCheck size={28} className="text-green-500" />} title="Completadas" value={stats.completadas} />
                <StatCard icon={<CreditCard size={28} className="text-yellow-500" />} title="Pendientes de Pago" value={stats.pendientes} />
                <StatCard icon={<XCircle size={28} className="text-red-500" />} title="Canceladas" value={stats.canceladas} />
            </div>

            {/* Zona de Gráficos */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">

                {/* Gráfico de Barras */}
                <div className="bg-white p-6 rounded-lg shadow-md border border-gray-100">
                    <h3 className="text-xl font-bold text-gray-700 mb-4">Evolución Semanal</h3>
                    <div className="h-72 min-h-[288px] w-full">
                        <ResponsiveContainer width="99%" height="100%">
                            <BarChart data={barData}>
                                <XAxis dataKey="name" stroke="#8884d8" />
                                <YAxis allowDecimals={false} />
                                <Tooltip
                                    cursor={{ fill: 'transparent' }}
                                    formatter={(value) => [value, 'Reservas']}
                                    labelFormatter={(label, payload) => {
                                        if (payload && payload[0]) return payload[0].payload.fecha;
                                        return label;
                                    }}
                                />
                                <Bar dataKey="reservas" fill="#1E3A8A" radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Gráfico de Pastel (Dona) */}
                <div className="bg-white p-6 rounded-lg shadow-md border border-gray-100">
                    <h3 className="text-xl font-bold text-gray-700 mb-4">Estado de Reservas</h3>
                    <div className="h-72 min-h-[288px] w-full">
                        {bookings.length > 0 ? (
                            <ResponsiveContainer width="99%" height="100%">
                                <PieChart>
                                    <Pie data={pieData} innerRadius={80} outerRadius={110} paddingAngle={5} dataKey="value">
                                        {pieData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={entry.color} />
                                        ))}
                                    </Pie>
                                    <Tooltip formatter={(value, name) => [value, name]} />
                                </PieChart>
                            </ResponsiveContainer>
                        ) : (
                            <div className="flex items-center justify-center h-full text-gray-400">
                                No hay reservas registradas
                            </div>
                        )}
                    </div>
                    {/* Leyenda del gráfico de dona */}
                    <div className="flex justify-center gap-4 mt-2 text-sm text-gray-600">
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#10B981]"></span> Completadas ({stats.completadas})</span>
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#F59E0B]"></span> Pendientes ({stats.pendientes})</span>
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#EF4444]"></span> Canceladas ({stats.canceladas})</span>
                    </div>
                </div>

            </div>
        </div>
    );
}

// Componente hijo para reutilizar el diseño de las tarjetitas
function StatCard({ title, value, icon }) {
    return (
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center space-x-4 hover:shadow-md transition-shadow">
            <div className="p-3 bg-gray-50 rounded-full">
                {icon}
            </div>
            <div>
                <p className="text-sm text-gray-500 font-medium">{title}</p>
                <p className="text-2xl font-bold text-gray-800">{value}</p>
            </div>
        </div>
    );
}