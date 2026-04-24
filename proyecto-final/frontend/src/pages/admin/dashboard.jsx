import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { CalendarCheck, CreditCard, XCircle, TrendingUp } from 'lucide-react';

// Datos falsos (Mocks) para la maquetación
const barData = [
    { name: 'Lun', reservas: 12 },
    { name: 'Mar', reservas: 19 },
    { name: 'Mié', reservas: 15 },
    { name: 'Jue', reservas: 22 },
    { name: 'Vie', reservas: 30 },
    { name: 'Sáb', reservas: 35 },
    { name: 'Dom', reservas: 25 },
];

const pieData = [
    { name: 'Completadas', value: 60, color: '#10B981' },
    { name: 'Pendientes', value: 30, color: '#F59E0B' },
    { name: 'Canceladas', value: 10, color: '#EF4444' },
];

export default function AdminDashboard() {
    return (
        <div className="max-w-7xl mx-auto space-y-6">

            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold text-gray-800">Dashboard General</h2>
            </div>

            {/* Tarjetas de Estadísticas Principales */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard icon={<TrendingUp size={28} className="text-blue-500" />} title="Reservas del Día" value="24" />
                <StatCard icon={<CalendarCheck size={28} className="text-green-500" />} title="Completadas" value="15" />
                <StatCard icon={<CreditCard size={28} className="text-yellow-500" />} title="Pendientes de Pago" value="7" />
                <StatCard icon={<XCircle size={28} className="text-red-500" />} title="Canceladas" value="2" />
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
                                <YAxis />
                                <Tooltip cursor={{ fill: 'transparent' }} />
                                <Bar dataKey="reservas" fill="#1E3A8A" radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Gráfico de Pastel (Dona) */}
                <div className="bg-white p-6 rounded-lg shadow-md border border-gray-100">
                    <h3 className="text-xl font-bold text-gray-700 mb-4">Estado de Reservas</h3>
                    <div className="h-72 min-h-[288px] w-full">
                        <ResponsiveContainer width="99%" height="100%">
                            <PieChart>
                                <Pie data={pieData} innerRadius={80} outerRadius={110} paddingAngle={5} dataKey="value">
                                    {pieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={entry.color} />
                                    ))}
                                </Pie>
                                <Tooltip />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                    {/* Leyenda del gráfico de dona */}
                    <div className="flex justify-center gap-4 mt-2 text-sm text-gray-600">
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#10B981]"></span> Completadas</span>
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#F59E0B]"></span> Pendientes</span>
                        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-[#EF4444]"></span> Canceladas</span>
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