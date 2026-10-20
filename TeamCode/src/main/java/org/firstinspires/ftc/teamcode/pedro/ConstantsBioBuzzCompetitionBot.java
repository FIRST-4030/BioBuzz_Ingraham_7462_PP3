package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ConstantsBioBuzzCompetitionBot {
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
//        c.frontLeftName.set("leftFront");
//        c.frontRightName.set("rightFront");
//        c.backLeftName.set("leftBack");
//        c.backRightName.set("rightBack");
//        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
//        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
//        c.powerThreshold.set(0.7);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
//        c.name.set("pinpoint");
//        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
//        c.xPodOffset.set(-3.0335559995155634);
//        c.yPodOffset.set(-4.14238696961891);
//        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
//        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
//        c.globalDistanceUnit.set(DistanceUnit.INCH);
//        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
//            Controller primaryTranslationalForward = Controller.proportional(0.1509352828568595);
//            Controller secondaryTranslationalForward = Controller.proportional(0.05576655395057363);
//            Controller primaryTranslationalLateral = Controller.proportional(0.22444284164231273);
//            Controller secondaryTranslationalLateral = Controller.proportional(0.08292563276365342);
//
//            c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
//            c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));
//
//            c.coast.set(Controller.proportionalFeedforward(0.016772725761226175));
//            c.brake.set(Controller.proportionalFeedforward(0.014256816897042248));
//
//            c.headingFeedback.set(Controller.proportional(2.5953331081788047));
//            c.headingBrakeCoefficients.set(Vector2D.cartesian(0.03161611130676878, 0.008101336146249187));
//
//            c.linearBrakeCoefficients.set(Matrix.diag(0.036235804170259386, 0.023882591887731595));
//            c.quadraticBrakeCoefficients.set(Matrix.diag(0.0021219223489505365, 0.002363874858326491));
//
//            c.maxAchievableForwardVelocity.set(61.561486912966416);
//            c.maxAchievableStrafeVelocity.set(49.98801354264194);
//            c.naturalForwardDeceleration.set(47.027844708111154);
//            c.naturalStrafeDeceleration.set(71.22551686300882);
        }
    );
}