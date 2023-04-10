CREATE TABLE public.lab_users
(
    id integer NOT NULL,
    os_username character varying(64) NOT NULL,
    os_password character varying(100) NOT NULL,
    lastname character varying(100),
    firstname character varying(100),
    patronymic character varying(100),
    study_group character varying(20),
    CONSTRAINT "pk_tbl.lab_users" PRIMARY KEY (id)
);

CREATE TABLE public.labs
(
    id integer NOT NULL,
    lab_name character varying(100) NOT NULL,
    app_name character varying(100) NOT NULL,
    CONSTRAINT pk_labs PRIMARY KEY (id)
);

CREATE TABLE public.bookings
(
    id bigint NOT NULL,
    lab_id integer NOT NULL,
    user_id integer NOT NULL,
    start_date_time timestamp with time zone NOT NULL,
    end_date_time timestamp with time zone NOT NULL,
    status character varying(20) NOT NULL,
    CONSTRAINT "pk_tbl.booking" PRIMARY KEY (id),
    CONSTRAINT fk_bookings_lab_users FOREIGN KEY (user_id)
        REFERENCES public.lab_users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_bookings_labs FOREIGN KEY (lab_id)
        REFERENCES public.labs (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

