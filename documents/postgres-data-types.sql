CREATE TABLE "test data types" (
	"Type SmallInt" SMALLINT,
	"Type Int" INT,
	"Type BigInt" BIGINT,

	"Type Decimal" DECIMAL,
	"Type Numeric" NUMERIC(16, 4), -- 16 numbers, 4 decimal
	"Type Real" REAL,
	"Type Double" DOUBLE PRECISION,

	"Type SmallSerial" SMALLSERIAL,
	"Type BigSerial" BIGSERIAL,

--	"Type Money" MONEY,

	"Type Char" CHAR(32),
	"Type VarChar" VARCHAR(32),
	"Type Text" TEXT,
	
	"Type ByteA" BYTEA,
	
	"Type TimeStamp" TIMESTAMP(6),
	"Type Date" DATE,
	"Type Time" TIME(6),
	
	"Type Boolean" BOOLEAN
);

INSERT INTO "test data types" 
(
	"Type SmallInt", "Type Int", "Type BigInt",
	"Type Decimal", "Type Numeric", "Type Real", "Type Double",
	"Type SmallSerial", "Type BigSerial",
--	"Type Money",
	"Type Char", "Type VarChar", "Type Text",
	"Type ByteA",
	"Type TimeStamp", "Type Date", "Type Time",
	"Type Boolean"
) 
VALUES 
(
	1, 2, 3,
	4.4, 5.5, 6.6, 7.7,
	8, 9,
--	10.10,
	'A', 'B', 'C',
	decode('deadbeef', 'hex'),
	'2000-12-31 13:01:59.123456', '2001-12-31', '14:59:59.123456',
	TRUE
);

SELECT * FROM "test data types";

/*
index,ColumnLabel,ColumnClassName,ColumnType,ColumnTypeName,Value,Class
1,Type SmallInt,java.lang.Integer,5,int2,1,java.lang.Integer
2,Type Int,java.lang.Integer,4,int4,2,java.lang.Integer
3,Type BigInt,java.lang.Long,-5,int8,3,java.lang.Long
4,Type Decimal,java.math.BigDecimal,2,numeric,4.4,java.math.BigDecimal
5,Type Numeric,java.math.BigDecimal,2,numeric,5.5000,java.math.BigDecimal
6,Type Real,java.lang.Float,7,float4,6.6,java.lang.Float
7,Type Double,java.lang.Double,8,float8,7.7,java.lang.Double
8,Type SmallSerial,java.lang.Integer,5,smallserial,8,java.lang.Integer
9,Type BigSerial,java.lang.Long,-5,bigserial,9,java.lang.Long
10,Type Money,org.postgresql.util.PGmoney,8,money,10.1,java.lang.Double
11,Type Char,java.lang.String,1,bpchar,A                               ,java.lang.String
12,Type VarChar,java.lang.String,12,varchar,B,java.lang.String
13,Type Text,java.lang.String,12,text,C,java.lang.String
14,Type ByteA,[B,-2,bytea,[B@1f1c7bf6,byte[]
15,Type TimeStamp,java.sql.Timestamp,93,timestamp,2000-12-31 13:01:59.123456,java.sql.Timestamp
16,Type Date,java.sql.Date,91,date,2001-12-31,java.sql.Date
17,Type Time,java.sql.Time,92,time,14:59:59,java.sql.Time
18,Type Boolean,java.lang.Boolean,-7,bool,true,java.lang.Boolean
*/

CREATE TABLE "test versioned rows" (
	"GUID" BYTEA PRIMARY KEY,
	"Version" BIGINT NOT NULL,

	"Type SmallInt" SMALLINT,
	"Type Int" INT,
	"Type BigInt" BIGINT,

	"Type Decimal" DECIMAL,
	"Type Numeric" NUMERIC(16, 4), -- 16 numbers, 4 decimal
	"Type Real" REAL,
	"Type Double" DOUBLE PRECISION,

	"Type SmallSerial" SMALLSERIAL,
	"Type BigSerial" BIGSERIAL,

--	"Type Money" MONEY,

	"Type Char" CHAR(32),
	"Type VarChar" VARCHAR(32),
	"Type Text" TEXT,
	
	"Type ByteA" BYTEA,
	
	"Type TimeStamp" TIMESTAMP(6),
	"Type Date" DATE,
	"Type Time" TIME(6),
	
	"Type Boolean" BOOLEAN
);

INSERT INTO "test versioned rows" 
(
	"GUID", "Version",
	"Type SmallInt", "Type Int", "Type BigInt",
	"Type Decimal", "Type Numeric", "Type Real", "Type Double",
	"Type SmallSerial", "Type BigSerial",
--	"Type Money",
	"Type Char", "Type VarChar", "Type Text",
	"Type ByteA",
	"Type TimeStamp", "Type Date", "Type Time",
	"Type Boolean"
) 
VALUES 
(
	decode('deadbeefdeadbeefdeadbeefdeadbeef', 'hex'), 1,
	1, 2, 3,
	4.4, 5.5, 6.6, 7.7,
	8, 9,
--	10.10,
	'A', 'B', 'C',
	decode('deadbeef', 'hex'),
	'2000-12-31 13:01:59.123456', '2001-12-31', '14:59:59.123456',
	TRUE
);

SELECT * FROM "test versioned rows";
