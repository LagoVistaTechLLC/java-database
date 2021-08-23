CREATE TABLE "%TABLE%" (
	"Type UUID" UUID,

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

INSERT INTO "%TABLE%" 
(
	"Type UUID",
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
	'ea7cbaac-35ee-4547-95eb-3112f16f2cff',
	1, 2, 3,
	4.4, 5.5, 6.6, 7.7,
	8, 9,
--	10.10,
	'A', 'B', 'C',
	decode('deadbeef', 'hex'),
	'2000-12-31 13:01:59.123', '2001-12-31', '14:59:59.123',
	TRUE
);