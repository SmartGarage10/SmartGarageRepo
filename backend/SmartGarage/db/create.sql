create or replace table role
(
    role_id   bigint auto_increment
        primary key,
    role_name varchar(50) not null
);

create or replace table service
(
    service_id int auto_increment
        primary key,
    price      double       not null,
    name       varchar(100) not null
);

create or replace table users
(
    user_id  int auto_increment
        primary key,
    email    varchar(255) not null,
    password varchar(255) not null,
    phone    varchar(255) not null,
    username varchar(255) not null,
    role_id  bigint       not null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint UKdu5v5sr43g5bfnji4vb8hg5s3
        unique (phone),
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username),
    constraint FK4qu1gr772nnf6ve5af002rwya
        foreign key (role_id) references role (role_id)
);

create or replace table vehicles
(
    vehicle_id    int auto_increment
        primary key,
    vehicle_plate varchar(255) not null,
    vin           varchar(255) not null,
    client_id     int          not null,
    constraint FKblvagddu2q2y44h5bkjhq4g6g
        foreign key (client_id) references users (user_id)
);

create or replace table service_order
(
    order_id    int auto_increment
        primary key,
    amount      double       not null,
    currency    varchar(255) not null,
    order_date  datetime(6)  not null,
    status      varchar(255) not null,
    employee_id int          not null,
    vehicle_id  int          not null,
    constraint FK3pqclp2qjse9r1xdbblb0faoe
        foreign key (vehicle_id) references vehicles (vehicle_id),
    constraint FKn30961sqo6g03fvo9fw93onp7
        foreign key (employee_id) references users (user_id)
);

create or replace table payment
(
    payment_id int auto_increment
        primary key,
    method     varchar(255) not null,
    status     varchar(255) not null,
    order_id   int          not null,
    constraint FKltdajm3icefk59pu6y9wfr2fp
        foreign key (order_id) references service_order (order_id)
);

create or replace table service_order_details
(
    service_order_detail_id int auto_increment
        primary key,
    order_id                int not null,
    service_id              int not null,
    constraint FKhftam7ri49x1coragygjm43q2
        foreign key (order_id) references service_order (order_id),
    constraint FKny8bkhy0abu2cqs03wb4mad1
        foreign key (service_id) references service (service_id)
);

create or replace table visits
(
    visit_id    bigint auto_increment
        primary key,
    date        datetime(6) not null,
    employee_id int         not null,
    vehicle_id  int         not null,
    constraint FK7ivnjshpy6nbpwygou1rkkdn0
        foreign key (vehicle_id) references vehicles (vehicle_id),
    constraint FKebututc3v21gk4qgusuu0r7ea
        foreign key (employee_id) references users (user_id)
);

