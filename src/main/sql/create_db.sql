BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE if EXISTS auction_order CASCADE;
DROP TABLE if EXISTS fixed_order CASCADE;
DROP TABLE if EXISTS user_sg_mapping CASCADE;
DROP TABLE if EXISTS fixed_listing CASCADE;
DROP TABLE if EXISTS bid CASCADE;
DROP TABLE if EXISTS auction_listing CASCADE;
DROP TABLE if EXISTS seller_group CASCADE;
DROP TABLE if EXISTS app_user CASCADE;

CREATE TABLE IF NOT EXISTS public.app_user
(
    username character varying NOT NULL,
    create_timestamp timestamp without time zone NOT NULL,
    email character varying NOT NULL,
    pwd character varying NOT NULL,
    role character varying NOT NULL,
    address character varying NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS public.seller_group
(
    sg_id uuid NOT NULL,
    name character varying(80) NOT NULL UNIQUE,
    PRIMARY KEY (sg_id)
);

CREATE TABLE IF NOT EXISTS public.user_sg_mapping
(
    username character varying NOT NULL,
    sg_id uuid NOT NULL,
    PRIMARY KEY (username, sg_id),
    FOREIGN KEY (username) REFERENCES public.app_user(username) ON DELETE CASCADE,
    FOREIGN KEY (sg_id) REFERENCES seller_group(sg_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.fixed_listing
(
    listing_id uuid NOT NULL,
    sg_id uuid NOT NULL,
    create_timestamp timestamp without time zone NOT NULL,
    price numeric NOT NULL,
    description text,
    condition character varying NOT NULL,
    quantity integer NOT NULL,
    PRIMARY KEY (listing_id),
    FOREIGN KEY (sg_id) REFERENCES seller_group(sg_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.fixed_order
(
    order_id uuid NOT NULL,
    username character varying NOT NULL,
    listing_id uuid NOT NULL,
    create_timestamp timestamp without time zone NOT NULL,
    quantity integer NOT NULL,
    total numeric NOT NULL,
    address character varying(255) NOT NULL,
    PRIMARY KEY (order_id),
    FOREIGN KEY (username) REFERENCES public.app_user(username) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES fixed_listing(listing_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.auction_listing
(
    listing_id uuid NOT NULL,
    sg_id uuid NOT NULL,
    create_timestamp timestamp without time zone NOT NULL,
    start_price numeric DEFAULT 0,
    description text,
    end_timestamp timestamp with time zone,
    condition character varying NOT NULL,
    PRIMARY KEY (listing_id),
    FOREIGN KEY (sg_id) REFERENCES seller_group(sg_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.bid
(
    bid_id uuid NOT NULL,
    username character varying NOT NULL,
    listing_id uuid NOT NULL,
    create_timestamp timestamp without time zone NOT NULL,
    value numeric NOT NULL,
    PRIMARY KEY (bid_id),
    FOREIGN KEY (username) REFERENCES public.app_user(username) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES auction_listing(listing_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.auction_order
(
    order_id uuid NOT NULL,
    username character varying NOT NULL,
    listing_id uuid NOT NULL,
    bid_id uuid,
    create_timestamp timestamp without time zone NOT NULL,
    address character varying NOT NULL,
    PRIMARY KEY (order_id),
    FOREIGN KEY (username) REFERENCES public.app_user(username) ON DELETE CASCADE ,
    FOREIGN KEY (listing_id) REFERENCES auction_listing(listing_id) ON DELETE CASCADE ,
    FOREIGN KEY (bid_id) REFERENCES bid(bid_id) ON DELETE CASCADE
);

END;