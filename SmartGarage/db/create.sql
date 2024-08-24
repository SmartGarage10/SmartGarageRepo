create table role
(
    role_id   int auto_increment
        primary key,
    role_name varchar(50) not null
);

create table services
(
    service_id   int auto_increment
        primary key,
    service_name varchar(255)   not null,
    price        decimal(10, 2) not null
);

create table users
(
    user_id  int auto_increment
        primary key,
    username varchar(20)  not null,
    password varchar(255) not null,
    email    varchar(255) not null,
    phone    char(10)     not null,
    role_id  int          null,
    constraint users_ibfk_1
        foreign key (role_id) references role (role_id)
);

create index role_id
    on users (role_id);

create table vehicles
(
    vehicle_id    int auto_increment
        primary key,
    vehicle_plate varchar(8)  not null,
    vin           varchar(17) not null,
    year          int         not null
        check (`year` > 1886),
    brand         varchar(20) null,
    mdel          varchar(50) not null,
    client_id     int         not null,
    constraint client_id_fk
        foreign key (client_id) references users (user_id)
);

create table service_orders
(
    order_id    int auto_increment
        primary key,
    vehicle_id  int         not null,
    employee_id int         not null,
    date        timestamp   not null,
    status      varchar(10) not null,
    amount      decimal     not null,
    currency    varchar(3)  not null,
    constraint service_orders_vehicle_id_fk
        foreign key (vehicle_id) references vehicles (vehicle_id)
);

create table payments
(
    payment_id     int auto_increment
        primary key,
    order_id       int         not null,
    payment_method varchar(20) not null,
    status         varchar(10) not null,
    constraint payments_order_id_fk
        foreign key (order_id) references service_orders (order_id)
);

create table service_order_details
(
    service_order_detail_id int auto_increment
        primary key,
    order_id                int not null,
    service_id              int null,
    constraint service_order_details
        foreign key (order_id) references service_orders (order_id),
    constraint service_order_details_service_id_fk
        foreign key (service_id) references services (service_id)
);

create table visits
(
    visit_id    int auto_increment
        primary key,
    employee_id int       not null,
    vehicle_id  int       not null,
    date        timestamp not null,
    constraint vehicle_id_fk
        foreign key (vehicle_id) references vehicles (vehicle_id)
);